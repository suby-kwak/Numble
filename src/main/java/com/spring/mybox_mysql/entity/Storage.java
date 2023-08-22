package com.spring.mybox_mysql.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"user","parentNo"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "storage")
@EntityListeners(AuditingEntityListener.class)
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storageno")
    private Long storageNo;

    @OneToOne
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentno")
    private Storage parentNo;

    @OneToMany(mappedBy = "parentNo", cascade = CascadeType.ALL)
    private List<Storage> child;

    @Column(name = "foldername")
    private String folderName;

    @CreatedDate
    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "storageNo")
    private List<UserFile> files;
}
