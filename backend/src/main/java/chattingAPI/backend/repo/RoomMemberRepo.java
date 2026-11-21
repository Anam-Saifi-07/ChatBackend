package chattingAPI.backend.repo;

import chattingAPI.backend.entity.RoomMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepo extends JpaRepository<RoomMembers,RoomMembers.RoomMemberId> {
    List<RoomMembers> findByRoomId(String roomId);
    List<RoomMembers> findByPersonId(String personId);
    // kya user already is room ka member hai?
    boolean existsByPersonIdAndRoomId(String personId, String roomId);
}
