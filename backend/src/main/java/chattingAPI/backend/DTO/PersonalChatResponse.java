package chattingAPI.backend.DTO;

import chattingAPI.backend.entity.Person;
import chattingAPI.backend.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalChatResponse {
    private Room room;
    private Person person;
}
