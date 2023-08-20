package com.spring.mybox_mysql.repository;

import com.spring.mybox_mysql.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.storageSize = ?2 WHERE u.userId = ?1")
    void updateStorage(String userId, long storageSize);

}
