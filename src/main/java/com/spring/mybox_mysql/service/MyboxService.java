package com.spring.mybox_mysql.service;

import com.spring.mybox_mysql.entity.Storage;
import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.entity.UserFile;
import com.spring.mybox_mysql.repository.StorageRepository;
import com.spring.mybox_mysql.repository.UserFileRepository;
import com.spring.mybox_mysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyboxService {

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFileRepository fileRepository;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    // 유저 생성
    public User saveUser(String userId, String name, String password) {
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

            storageRepository.save(storage);

            return userRepository.save(user);
        }
        return null;
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
    public Storage saveChild(String userId, Long parentNo, String folderName) {
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

            return storageRepository.save(storage);
        }
        return null;
    }

    // 폴더 삭제
    public void deleteChild(Long storageNo) {
        storageRepository.deleteById(storageNo);
    }

    // 폴더 다운로드


    // 파일 다운로드
    public UserFile findByNo(Long fileNo) {
        Optional<UserFile> file = fileRepository.findById(fileNo);

        if (file.isEmpty()) {
            return null;
        }

        return file.orElseThrow();
    }


    // 파일 업로드
    @Transactional
    public UserFile saveFile(MultipartFile file) throws IOException {
        String path = "D:/test/";
        String fileName = UUID.randomUUID().toString();

        Optional<Storage> storage = findByUserAndFolderName("rhkdbtj@naver.com", "root");

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
