package chattingAPI.backend.repo;

import chattingAPI.backend.entity.Message;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message,Long> {
    List<Message> findAllById(Long id);

    List<Message> findByRoomIdOrderByCreatedAtAsc(String roomId, Pageable pageable);

}
