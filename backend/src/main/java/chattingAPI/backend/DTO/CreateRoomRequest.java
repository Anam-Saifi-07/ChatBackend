package chattingAPI.backend.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.SpringVersion;

import java.util.List;

@Data
@Getter
@Setter
public class CreateRoomRequest {
    private String name;
    private Boolean isGroup;

    @NotEmpty(message = "Room must have at least one target member")
    private List<String> memberIds; // Target user IDs to add in room
}
