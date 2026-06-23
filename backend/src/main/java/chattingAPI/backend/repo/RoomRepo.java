package chattingAPI.backend.repo;

import chattingAPI.backend.entity.Message;
import chattingAPI.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room,String> {

}
