package chattingAPI.backend.entity;

import jakarta.persistence.*;
import lombok.*;
//import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    private String id;

    @Column(nullable = false)
    private  String name;

    @CreationTimestamp
    private Date createdAt;

    @Column(nullable = false)
    private boolean isGroup;

}
