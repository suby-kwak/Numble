package com.spring.mybox_mysql.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.*;
import com.spring.mybox_mysql.config.FileUtil;
import com.spring.mybox_mysql.config.XferMgrProgress;
import com.spring.mybox_mysql.entity.Storage;
import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.entity.UserFile;
import com.spring.mybox_mysql.repository.StorageRepository;
import com.spring.mybox_mysql.repository.UserFileRepository;
import com.spring.mybox_mysql.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyboxService {

    private final StorageRepository storageRepository;

    private final UserRepository userRepository;

    private final UserFileRepository fileRepository;

    private final AmazonS3 amazonS3;

    private final TransferManager transferManager;

    @Value("${cloud.aws.credentials.bucket}")
    private String bucketName;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    // 유저 생성
    public void saveUser(String userId, String name, String password) {
        if(userRepository.findById(userId).isEmpty()) {
            User user = User.builder()
                    .userId(userId)
                    .userName(name)
                    .password(password)
                    .storageSize(32212254720l)
                    .build();

            Storage storage = Storage.builder()
                    .user(user)
                    .folderName("root")
                    .build();

            userRepository.save(user);

            storageRepository.save(storage);
        }
    }

    // 유저 삭제
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    // 폴더 찾기
    public Optional<Storage> findByUserAndFolderName(String userId, String folderName) {
        return storageRepository.findByUserAndAndFolderName(userId, folderName);
    }

    // 하위 폴더 생성
    public void saveChild(String userId, Long parentNo, String folderName) {
        if(!storageRepository.findById(parentNo).orElseThrow().getFolderName().equals(folderName)) {
            User user = new User();
            user.setUserId(userId);

            Storage parent = new Storage();
            parent.setStorageNo(parentNo);

            Storage storage = Storage.builder()
                    .user(user)
                    .parentNo(parent)
                    .folderName(folderName)
                    .build();

            storageRepository.save(storage);
        }
    }

    // 폴더 삭제
    public void deleteChild(Long storageNo) {
        storageRepository.deleteById(storageNo);
    }

    // 폴더 다운로드


    // 파일 다운로드
    public ResponseEntity<Resource> fileDownload(Long fileNo) {
        Resource resource = null;
        HttpHeaders headers = null;
        try {
            UserFile file = fileRepository.findById(fileNo).orElseThrow();
            Path path = Paths.get(file.getPath());
            resource = new InputStreamResource(Files.newInputStream(path));

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(URLEncoder.encode(file.getFileOriginName(),"UTF-8")).build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }


    // Object Storage 파일 업로드
    @Transactional
    public UserFile uploadfile(MultipartFile file, String userId) throws IOException {
        String fileName = UUID.randomUUID().toString();
        long storageSize = userRepository.findById(userId).orElseThrow().getStorageSize();

        userRepository.updateStorage(userId, storageSize - file.getSize());

        Optional<Storage> storage = findByUserAndFolderName(userId, "root");

        if(!file.isEmpty() && !storage.isEmpty()) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);

            UserFile userFile = UserFile.builder()
                    .storageNo(storage.orElseThrow())
                    .fileName(fileName)
                    .fileOriginName(file.getOriginalFilename())
                    .filesize(file.getSize())
                    .contentType(file.getContentType())
                    .path(amazonS3.getUrl(bucketName, fileName).toString()).build();

            return fileRepository.save(userFile);
        }

        return null;
    }

    // Object Storage 파일 다운로드(trouble shooting : java.lang.outofmemoryerror: java heap space)
//    public ResponseEntity<byte[]> downloadFile(Long fileNo) {
//        byte[] bytes = null;
//        HttpHeaders headers = null;
//        try {
//            UserFile file = fileRepository.findById(fileNo).orElseThrow();
//            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, file.getFileName()));
//            S3ObjectInputStream inputStream = s3Object.getObjectContent();
//            bytes = IOUtils.toByteArray(inputStream);
//
//            headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDisposition(ContentDisposition.builder("attachment")
//                    .filename(URLEncoder.encode(file.getFileOriginName(),"UTF-8")).build());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
//    }

    public ResponseEntity<Resource> download2(Long fileNo) throws IOException, InterruptedException {
        Resource resource = null;
        HttpHeaders headers = null;
        UserFile userFile = fileRepository.findById(fileNo).orElseThrow();
        File file = new File(userFile.getFileOriginName());
        Path path = Paths.get(file.getPath());
        try {
            Download downloadDirectory = transferManager.download(bucketName, userFile.getFileName(), file);

            // 다운로드 상태 확인
            log.info("[" + userFile.getFileOriginName() + "] download progressing... start");
            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            while (!downloadDirectory.isDone()) {
                Thread.sleep(1000);
                TransferProgress progress = downloadDirectory.getProgress();
                double percentTransferred = progress.getPercentTransferred();
                log.info("[" + userFile.getFileOriginName() + "] " + decimalFormat.format(percentTransferred) + "% download progressing...");
            }
            log.info("[" + userFile.getFileOriginName() + "] download directory from S3 success!");

            resource = new InputStreamResource(Files.newInputStream(path));
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(URLEncoder.encode(userFile.getFileOriginName(),"UTF-8")).build());
        } finally {
            FileUtil.remove(file);
        }

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    // 대용량 파일 다운로드(프로젝트에 저장)
    public void download(Long fileNo) {
        UserFile file = fileRepository.findById(fileNo).orElseThrow();

        File f = new File(file.getFileOriginName());
        try {
            Download xfer = transferManager.download(bucketName, file.getFileName(), f);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();
    }

    // 파일 삭제
}
