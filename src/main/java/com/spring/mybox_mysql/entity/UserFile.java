package com.spring.mybox_mysql.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "storageNo")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "userfile")
@EntityListeners(AuditingEntityListener.class)
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fileno")
    private Long fileNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storageno")
    private Storage storageNo;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "fileoriginname")
    private String fileOriginName;

    @Column(name = "filesize")
    private long filesize;

    @Column(name = "contenttype")
    private String contentType;

    private String path;

    @CreatedDate
    @Column(name = "createdat")
    private LocalDateTime createdAt;
}
