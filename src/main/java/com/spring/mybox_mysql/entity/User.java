package com.spring.mybox_mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User {
    @Id
    @Column(name = "userid")
    private String userId;

    private String password;

    @Column(name = "username")
    private String userName;

    @Column(name = "storagesize")
    private long storageSize;

    @OneToOne(mappedBy = "user")
    private Storage root;
}
