package com.spring.mybox_mysql.repository;

import com.spring.mybox_mysql.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    @Query("select s from Storage s where s.user.userId = ?1 and s.folderName = ?2")
    Optional<Storage> findByUserAndAndFolderName(String userId, String folderName);
}
