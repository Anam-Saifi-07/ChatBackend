package chattingAPI.backend.DTO;

import chattingAPI.backend.config.ConnectionStatus;
import chattingAPI.backend.config.ConnectionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionResponse {
    private String id;
    private ConnectionType type;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String roomId;
    private String roomName;
    private ConnectionStatus status;
    private Date createdAt;
}
