package chattingAPI.backend.entity;

import chattingAPI.backend.config.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class RoomMembers {

    @EmbeddedId
    private RoomMemberId id;

    private Roles role;

    @ManyToOne
    @MapsId("personId")
    @JoinColumn(name="person_id")
    private Person person;

    @ManyToOne
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private Room room;

    @CreationTimestamp
    private Date date;

    // Composite Key Definition
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RoomMemberId implements Serializable {
        private String personId;
        private String roomId;
    }

}
