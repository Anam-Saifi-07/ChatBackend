package chattingAPI.backend.repo;

import chattingAPI.backend.entity.Message;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message,Long> {
    List<Message> findAllById(String id);

    List<Message> findAllByRoomID(String roomId);
//    List<Message> findAllByRoomID_RoomId(String roomId);
    //    public Message SaveMsgByPersonId(int personId);
//    public String GetMsgContent(String id);
}
