package chattingAPI.backend.Service;

import chattingAPI.backend.DTO.ConnectionResponse;
import chattingAPI.backend.DTO.GroupJoinRequest;
import chattingAPI.backend.DTO.PersonalChatResponse;
import chattingAPI.backend.DTO.SendPersonalRequest;
import chattingAPI.backend.config.ConnectionStatus;
import chattingAPI.backend.config.ConnectionType;
import chattingAPI.backend.config.Roles;
import chattingAPI.backend.entity.Connection;
import chattingAPI.backend.entity.Person;
import chattingAPI.backend.entity.Room;
import chattingAPI.backend.entity.RoomMembers;
import chattingAPI.backend.repo.ConnectionRepo;
import chattingAPI.backend.repo.PersonRepo;
import chattingAPI.backend.repo.RoomMemberRepo;
import chattingAPI.backend.repo.RoomRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    private final ConnectionRepo connectionRepo;
    private final PersonRepo personRepo;
    private final RoomRepo roomRepo;
    private final RoomMemberRepo roomMemberRepo;

    public ConnectionService(ConnectionRepo connectionRepo,
                             PersonRepo personRepo,
                             RoomRepo roomRepo,
                             RoomMemberRepo roomMemberRepo) {
        this.connectionRepo = connectionRepo;
        this.personRepo = personRepo;
        this.roomRepo = roomRepo;
        this.roomMemberRepo = roomMemberRepo;
    }

    // ==================== PERSONAL ====================

    @Transactional
    public ConnectionResponse sendPersonalRequest(SendPersonalRequest request, String currentUsername) {
        Person sender = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Person receiver = personRepo.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Khud ko request nahi bhej sakte");
        }

        boolean alreadyConnected =
                connectionRepo.existsBySenderIdAndReceiverIdAndStatus(sender.getId(), receiver.getId(), ConnectionStatus.ACCEPTED)
                        || connectionRepo.existsBySenderIdAndReceiverIdAndStatus(receiver.getId(), sender.getId(), ConnectionStatus.ACCEPTED);
        if (alreadyConnected) {
            throw new RuntimeException("Pehle se connected ho");
        }

        boolean reversePending = connectionRepo
                .findBySenderIdAndReceiverIdAndStatus(receiver.getId(), sender.getId(), ConnectionStatus.PENDING)
                .isPresent();
        if (reversePending) {
            acceptBothSides(sender.getId(), receiver.getId());
            Connection connection = connectionRepo
                    .findBySenderIdAndReceiverIdAndStatus(sender.getId(), receiver.getId(), ConnectionStatus.ACCEPTED)
                    .orElseThrow();
            return toResponse(connection);
        }

        boolean duplicatePending = connectionRepo
                .findBySenderIdAndReceiverIdAndStatus(sender.getId(), receiver.getId(), ConnectionStatus.PENDING)
                .isPresent();
        if (duplicatePending) {
            throw new RuntimeException("Request already pending hai");
        }

        Connection connection = Connection.builder()
                .type(ConnectionType.PERSONAL)
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .roomId(null)
                .status(ConnectionStatus.PENDING)
                .build();

        try {
            connection = connectionRepo.save(connection);
        } catch (Exception e) {
            throw new RuntimeException("Duplicate request hai");
        }
        return toResponse(connection);
    }

    @Transactional
    public ConnectionResponse acceptConnection(String connectionId, String currentUsername) {
        Person current = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Connection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Request nahi mili"));

        if (!current.getId().equals(connection.getReceiverId())) {
            throw new RuntimeException("Sirf receiver accept kar sakta hai");
        }

        int updated = connectionRepo.updateStatusIfPending(connectionId, ConnectionStatus.ACCEPTED, ConnectionStatus.PENDING);
        if (updated == 0) {
            throw new RuntimeException("Request already processed ho chuki hai");
        }
        connection.setStatus(ConnectionStatus.ACCEPTED);

        if (connection.getType() == ConnectionType.PERSONAL) {
            acceptBothSides(connection.getSenderId(), connection.getReceiverId());
        } else {
            addMemberToGroup(connection);
        }
        return toResponse(connection);
    }

    @Transactional
    public ConnectionResponse rejectConnection(String connectionId, String currentUsername) {
        Person current = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Connection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Request nahi mili"));

        if (!current.getId().equals(connection.getReceiverId())) {
            throw new RuntimeException("Sirf receiver reject kar sakta hai");
        }

        int updated = connectionRepo.updateStatusIfPending(connectionId, ConnectionStatus.REJECTED, ConnectionStatus.PENDING);
        if (updated == 0) {
            throw new RuntimeException("Request already processed ho chuki hai");
        }
        connection.setStatus(ConnectionStatus.REJECTED);
        return toResponse(connection);
    }

    @Transactional
    public List<PersonalChatResponse> getAllPersonalChats(String currenUsername) {
        Person current = personRepo.findByName(currenUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> otherIds = new LinkedHashSet<>();

        for (Connection c : connectionRepo.findBySenderIdAndStatus(current.getId(), ConnectionStatus.ACCEPTED)) {
            if (c.getType() == ConnectionType.PERSONAL && c.getReceiverId() != null) {
                otherIds.add(c.getReceiverId());
            }
        }
        for (Connection c : connectionRepo.findByReceiverIdAndStatus(current.getId(), ConnectionStatus.ACCEPTED)) {
            if (c.getType() == ConnectionType.PERSONAL && c.getSenderId() != null) {
                otherIds.add(c.getSenderId());
            }
        }

        List<PersonalChatResponse> chats = new ArrayList<>();
        Set<String> addedRoomIds = new LinkedHashSet<>();
        for (String otherId : otherIds) {
            Person other = personRepo.findById(otherId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            for (RoomMembers otherMember : roomMemberRepo.findByPersonId(otherId)) {
                if (Boolean.TRUE.equals(otherMember.getRoom().getIsGroup())) {
                    continue;
                }
                if (otherMember.getRole() != Roles.MEMEBER) {
                    continue;
                }
                String roomId = otherMember.getId().getRoomId();
                boolean currentIsMember = roomMemberRepo.findByRoomId(roomId).stream()
                        .anyMatch(m -> m.getId().getPersonId().equals(current.getId())
                                && m.getRole() == Roles.MEMEBER);
                if (currentIsMember && addedRoomIds.add(roomId)) {
                    chats.add(new PersonalChatResponse(otherMember.getRoom(), other));
                }
            }
        }
        return chats;
    }

    // ==================== GROUP ====================

    @Transactional
    public ConnectionResponse sendGroupJoinRequest(GroupJoinRequest request, String currentUsername) {
        Person sender = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepo.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Group nahi mila"));

        if (!Boolean.TRUE.equals(room.getIsGroup())) {
            throw new RuntimeException("Ye group nahi hai");
        }
        if (roomMemberRepo.existsByPersonIdAndRoomId(sender.getId(), room.getId())) {
            throw new RuntimeException("Pehle se member ho");
        }

        // ConnectionService.java:
        boolean pendingExists = connectionRepo
                .findBySenderIdAndRoomIdAndStatus(sender.getId(), room.getId(), ConnectionStatus.PENDING)
                .isPresent();
        if (pendingExists) {
            throw new RuntimeException("Request already pending hai");
        }

        String ownerId = roomMemberRepo.findByRoomId(room.getId()).stream()
                .filter(m -> m.getRole() == Roles.ADMIN)
                .map(m -> m.getId().getPersonId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group admin nahi mila"));

        Connection connection = Connection.builder()
                .type(ConnectionType.GROUP)
                .senderId(sender.getId())
                .receiverId(ownerId)
                .roomId(room.getId())
                .status(ConnectionStatus.PENDING)
                .build();

        try {
            connection = connectionRepo.save(connection);
        } catch (Exception e) {
            throw new RuntimeException("Duplicate request hai");
        }
        return toResponse(connection);
    }

    // ==================== FETCH ====================

    public List<ConnectionResponse> getPendingIncoming(String currentUsername) {
        Person current = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return connectionRepo.findByReceiverIdAndStatus(current.getId(), ConnectionStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ConnectionResponse> getSentPending(String currentUsername) {
        Person current = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return connectionRepo.findBySenderIdAndStatus(current.getId(), ConnectionStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ConnectionResponse> getGroupPendingRequests(String roomId, String currentUsername) {
        Person current = personRepo.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = roomMemberRepo.findByRoomId(roomId).stream()
                .anyMatch(m -> m.getRole() == Roles.ADMIN && m.getId().getPersonId().equals(current.getId()));
        if (!isAdmin) {
            throw new RuntimeException("Sirf admin dekh sakta hai");
        }

        return connectionRepo.findByRoomIdAndStatus(roomId, ConnectionStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== HELPERS ====================

    private void acceptBothSides(String personA, String personB) {
        connectionRepo.findBySenderIdAndReceiverIdAndStatus(personA, personB, ConnectionStatus.PENDING)
                .ifPresent(c -> connectionRepo.updateStatusIfPending(c.getId(), ConnectionStatus.ACCEPTED, ConnectionStatus.PENDING));
        connectionRepo.findBySenderIdAndReceiverIdAndStatus(personB, personA, ConnectionStatus.PENDING)
                .ifPresent(c -> connectionRepo.updateStatusIfPending(c.getId(), ConnectionStatus.ACCEPTED, ConnectionStatus.PENDING));
        createPersonalRoom(personA, personB);
    }

    private void createPersonalRoom(String personA, String personB) {
        boolean roomExists = roomMemberRepo.findByPersonId(personA).stream()
                .filter(m -> Boolean.FALSE.equals(m.getRoom().getIsGroup()))
                .anyMatch(m -> roomMemberRepo.findByRoomId(m.getRoom().getId()).stream()
                        .anyMatch(m2 -> m2.getId().getPersonId().equals(personB)));
        if (roomExists) {
            return;
        }

        Person userA = personRepo.findById(personA).orElseThrow();
        Person userB = personRepo.findById(personB).orElseThrow();

        Room room = roomRepo.save(Room.builder()
                .name("")
                .isGroup(false)
                .build());

        roomMemberRepo.save(RoomMembers.builder()
                .id(new RoomMembers.RoomMemberId(userA.getId(), room.getId()))
                .person(userA)
                .room(room)
                .role(Roles.MEMEBER)
                .build());

        roomMemberRepo.save(RoomMembers.builder()
                .id(new RoomMembers.RoomMemberId(userB.getId(), room.getId()))
                .person(userB)
                .room(room)
                .role(Roles.MEMEBER)
                .build());
    }

    private void addMemberToGroup(Connection connection) {
        Room room = roomRepo.findById(connection.getRoomId())
                .orElseThrow(() -> new RuntimeException("Group nahi mila"));

        if (!Boolean.TRUE.equals(room.getIsGroup())) {
            throw new RuntimeException("Ye group nahi hai");
        }
        if (roomMemberRepo.existsByPersonIdAndRoomId(connection.getSenderId(), room.getId())) {
            return;
        }

        Person requester = personRepo.findById(connection.getSenderId()).orElseThrow();
        roomMemberRepo.save(RoomMembers.builder()
                .id(new RoomMembers.RoomMemberId(requester.getId(), room.getId()))
                .person(requester)
                .room(room)
                .role(Roles.MEMEBER)
                .build());
    }

    private ConnectionResponse toResponse(Connection c) {
        ConnectionResponse res = new ConnectionResponse();
        res.setId(c.getId());
        res.setType(c.getType());
        res.setSenderId(c.getSenderId());
        res.setReceiverId(c.getReceiverId());
        res.setRoomId(c.getRoomId());
        res.setStatus(c.getStatus());
        res.setCreatedAt(c.getCreatedAt());

        personRepo.findById(c.getSenderId()).ifPresent(p -> res.setSenderName(p.getName()));
        if (c.getRoomId() != null && !c.getRoomId().isEmpty()) {
            roomRepo.findById(c.getRoomId()).ifPresent(r -> res.setRoomName(r.getName()));
        }
        return res;
    }
}
