package chattingAPI.backend.repo;

import chattingAPI.backend.config.ConnectionStatus;
import chattingAPI.backend.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
public interface ConnectionRepo extends JpaRepository<Connection,String> {

    // mere aaye hue pending requests
    List<Connection> findByReceiverIdAndStatus(String receiverId, ConnectionStatus status);

    // maine bheji hue pending requests
    List<Connection> findBySenderIdAndStatus(String senderId, ConnectionStatus status);

    // group ke pending join requests
    List<Connection> findByRoomIdAndStatus(String roomId, ConnectionStatus status);

    // kya already accepted connection hai? (duplicate friend check)
    boolean existsBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, ConnectionStatus status);

    // kya is group mein already pending/accepted request hai?
    Optional<Connection> findBySenderIdAndRoomId(String senderId, String roomId);

    // find by id + receiver check ke liye
    Optional<Connection> findByIdAndReceiverId(String id, String receiverId);

    // specific connection find (reverse pending check ke liye)
    Optional<Connection> findBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, ConnectionStatus status);

    // ConnectionRepo.java:
    Optional<Connection> findBySenderIdAndRoomIdAndStatus(String senderId, String roomId, ConnectionStatus status);

    // GUARDED UPDATE — double accept se bachata hai
    // rows affected = 0 matlab pehle se processed ho chuka
    @Modifying
    @Transactional
    @Query("UPDATE Connection c SET c.status = :status, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :id AND c.status = :expectedStatus")
    int updateStatusIfPending(@Param("id") String id,
                              @Param("status") ConnectionStatus status,
                              @Param("expectedStatus") ConnectionStatus expectedStatus);
}
