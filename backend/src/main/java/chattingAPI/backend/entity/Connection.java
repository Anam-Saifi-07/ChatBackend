package chattingAPI.backend.entity;

import chattingAPI.backend.config.ConnectionStatus;
import chattingAPI.backend.config.ConnectionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
@Entity
@Table(
        name = "connection",

        indexes = {
                @Index(name = "idx_sender", columnList = "sender_id"),
                @Index(name = "idx_receiver_status", columnList = "receiver_id, status"),
                @Index(name = "idx_room_status", columnList = "room_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConnectionType type;

    @Column(name = "sender_id", nullable = false, length = 36)
    private String senderId;

    @Column(name = "receiver_id", length = 36)
    private String receiverId;

    // personal -> "" , group -> room id
    @Column(name = "room_id",  length = 36)
//    @Builder.Default
    private String roomId ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
//    @Builder.Default
    private ConnectionStatus status = ConnectionStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}


