package com.spring.mybox_mysql.service;

import com.spring.mybox_mysql.entity.Storage;
import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.entity.UserFile;
import com.spring.mybox_mysql.repository.StorageRepository;
import com.spring.mybox_mysql.repository.UserFileRepository;
import com.spring.mybox_mysql.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyboxService {

    private final StorageRepository storageRepository;

    private final UserRepository userRepository;

    private final UserFileRepository fileRepository;

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


    // 파일 업로드
    @Transactional
    public UserFile saveFile(MultipartFile file, String userId) throws IOException {
        String path = "E:/test/";
        String fileName = UUID.randomUUID().toString();
        long storageSize = userRepository.findById(userId).orElseThrow().getStorageSize();


        userRepository.updateStorage(userId, storageSize - file.getSize());

        Optional<Storage> storage = findByUserAndFolderName(userId, "root");

        if(!file.isEmpty() && !storage.isEmpty()) {
            UserFile userFile = UserFile.builder()
                    .storageNo(storage.orElseThrow())
                    .fileName(fileName)
                    .fileOriginName(file.getOriginalFilename())
                    .filesize(file.getSize())
                    .contentType(file.getContentType())
                    .path(path + fileName).build();

            file.transferTo(new File(userFile.getPath()));

            return fileRepository.save(userFile);
        }

        return null;
    }

    // 파일 삭제
}
