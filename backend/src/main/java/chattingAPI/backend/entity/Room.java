package chattingAPI.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
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
    private String roomID;

    private  String name;

    @CreationTimestamp
    private Date createdAt;

    @OneToMany(mappedBy = "roomID")
    private List<Message> messageList;


}
