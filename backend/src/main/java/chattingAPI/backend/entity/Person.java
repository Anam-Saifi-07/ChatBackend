package chattingAPI.backend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    private String id;

    @Column(nullable = false)
    private String mail;

    @Column(nullable = false)
    private String name;

    @Column(name = "hash_password",nullable = false)
    private String password;

    private boolean isMale;
    private int age;

    @CreationTimestamp
    private Date createdAt;


}
