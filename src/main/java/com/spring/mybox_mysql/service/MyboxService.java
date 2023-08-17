package com.spring.mybox_mysql.service;

import com.spring.mybox_mysql.entity.Storage;
import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.repository.StorageRepository;
import com.spring.mybox_mysql.repository.UserFileRepository;
import com.spring.mybox_mysql.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyboxService {
    private StorageRepository storageRepository;
    private UserRepository userRepository;
    private UserFileRepository fileRepository;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

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

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public Optional<Storage> findByUserAndFolderName(String userId, String folderName) {
        return storageRepository.findByUserAndAndFolderName(userId, folderName);
    }

}
