package com.spring.mybox_mysql.service;

import com.spring.mybox_mysql.entity.Storage;
import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.entity.UserFile;
import com.spring.mybox_mysql.repository.StorageRepository;
import com.spring.mybox_mysql.repository.UserFileRepository;
import com.spring.mybox_mysql.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class MyboxServiceTest {
    @Autowired
    StorageRepository storageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserFileRepository fileRepository;

    @Test
    @Transactional
    public void user_test() {
        User user = User.builder()
                .userId("rhkdbtj@naver.com")
                .userName("rhkr")
                .password("asdf")
                .storageSize(32212254720l)
                .build();
//        userRepository.save(user);

        Optional<User> id = userRepository.findById("rhkdbtj@naver.com");
        User user1 = id.orElseThrow(IllegalAccessError::new);
        System.out.println(user1);

    }

    @Test
    @Transactional
    public void storage_test() {
        User user = new User();
        user.setUserId("rhkdbtj@naver.com");


        Storage storage = Storage.builder()
                .user(user)
                .folderName("root")
                .build();

//        storageRepository.save(storage);
        Optional<Storage> storage1 = storageRepository.findByUserAndAndFolderName("rhkdbtj@naver.com", "root");
        System.out.println(storage1.orElseThrow());
    }

    @Test
    public void File_test() {
        Storage storage = new Storage();
        storage.setStorageNo(4l);

        UserFile file = UserFile.builder()
                .fileName(UUID.randomUUID().toString())
                .fileOriginName("test.txt")
                .contentType("text.plain")
                .filesize(32l)
                .storageNo(storage)
                .path("D:/test").build();

        fileRepository.save(file);
    }
}
