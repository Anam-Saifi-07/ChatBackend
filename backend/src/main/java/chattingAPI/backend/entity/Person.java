package chattingAPI.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false,unique = true)

    private String mail;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(name = "hash_password",nullable = false,unique = true)
    private String password;

    private boolean isMale;
    private int age;

    @CreationTimestamp
    private Date createdAt;


}
