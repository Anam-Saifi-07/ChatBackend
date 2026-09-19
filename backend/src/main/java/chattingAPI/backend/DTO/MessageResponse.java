package chattingAPI.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String roomId;
    private String senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime createdAt;
}
