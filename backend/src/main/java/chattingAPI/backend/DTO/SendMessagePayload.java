package chattingAPI.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
//this is for websocket when user send on web socket connections
@Data
public class SendMessagePayload {
    @NotNull(message = "Room ID is required")
    private String roomId;

    @NotBlank(message = "Message content cannot be empty")
    private String content;
}
