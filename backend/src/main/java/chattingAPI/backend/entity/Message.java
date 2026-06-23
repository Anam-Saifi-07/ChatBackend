package chattingAPI.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private  Long id;

    private String content;

    @CreationTimestamp
    private Date createdAt;

//    @ManyToOne
//    @JoinColumn
    private String personID;

//    @ManyToOne
//    @JoinColumn
    private String roomID;

//    public void setPersonID(String personID) {
//       this.personID=personID;
//    }

//    public void setRoomID(String roomId) {
//    }
}
