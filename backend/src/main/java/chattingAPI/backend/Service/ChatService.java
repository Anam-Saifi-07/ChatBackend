package chattingAPI.backend.Service;

import chattingAPI.backend.DTO.*;
import chattingAPI.backend.config.Roles;
import chattingAPI.backend.entity.Message;
import chattingAPI.backend.entity.Person;
import chattingAPI.backend.entity.Room;
import chattingAPI.backend.entity.RoomMembers;
import chattingAPI.backend.repo.MessageRepo;
import chattingAPI.backend.repo.PersonRepo;
import chattingAPI.backend.repo.RoomMemberRepo;
import chattingAPI.backend.repo.RoomRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final RoomRepo chatRoomRepository;
    private final RoomMemberRepo roomMemberRepository;
    private final PersonRepo userRepository;
    private final MessageRepo messageRepository;

    public ChatService(RoomRepo chatRoomRepository,
                       RoomMemberRepo roomMemberRepository,
                       PersonRepo userRepository,
                       MessageRepo messageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request, String currentUsername) {
        Person creator = userRepository.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("Creator user not found"));

        // 1. ChatRoom create karo
        Room room = Room.builder()
                .name(request.getIsGroup() ? request.getName() : null)
                .isGroup(request.getIsGroup())
                .build();

        Room savedRoom = chatRoomRepository.save(room);

        // 2. Creator ko ADMIN role ke saath add karo
        RoomMembers creatorMember = RoomMembers.builder()
                .id(new RoomMembers.RoomMemberId(creator.getId(), savedRoom.getId()))
                .person(creator)
                .room(savedRoom)
                .role(Roles.ADMIN)
                .build();
        roomMemberRepository.save(creatorMember);

        // 3. Baaki target members ko MEMBER role ke saath junction table mein link karo
        for (String memberId : request.getMemberIds()) {
            if (!memberId.equals(creator.getId())) {
                Person memberUser = userRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Target user not found: " + memberId));

                RoomMembers member = RoomMembers.builder()
                        .id(new RoomMembers.RoomMemberId(memberUser.getId(), savedRoom.getId()))
                        .person(memberUser)
                        .room(savedRoom)
                        .role(Roles.MEMEBER)
                        .build();
                roomMemberRepository.save(member);
            }
        }

        return RoomResponse.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
                .isGroup(savedRoom.getIsGroup())
                .createdAt(savedRoom.getCreatedAt())
                .build();
    }

    public List<RoomResponse> getUserRooms(String currentUsername) {
        Person user = userRepository.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // User ke saare room memberships fetch karo
        List<RoomMembers> memberships = roomMemberRepository.findByPersonId(user.getId());

        return memberships.stream().map(membership -> {
            Room room = membership.getRoom();
            return RoomResponse.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .isGroup(room.getIsGroup())
                    .createdAt(room.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<MessageResponse> getRoomMessages(String roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Message> messages = messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);

        return messages.stream().map(msg -> MessageResponse.builder()
                .id(msg.getId())
                .roomId( msg.getRoom().getId())
                .senderId(msg.getPerson().getId())
                .senderUsername(msg.getPerson().getName())
                .content(msg.getContent())
                .createdAt(msg.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }

    //for websocket
    public MessageResponse saveAndFormatMessage(SendMessagePayload payload,String senderUserName){
        //1. check the sender
        Person sender=userRepository.findByName(senderUserName)
                .orElseThrow(()->new RuntimeException("Sender user not found"));
        //2. target room ko check karo
        Room room = chatRoomRepository.findById(payload.getRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        // 3. Message Entity create karo aur values assign karo
        Message message = Message.builder()
                .person(sender)
                .room(room)
                .content(payload.getContent())
                .build();
        //4. save on database
        Message savedMsg=messageRepository.save(message);
        //5.response dto mein set data for send back response to all subscribers
        return MessageResponse.builder()
                .id(savedMsg.getId())
                .roomId(savedMsg.getRoom().getId())
                .senderId(savedMsg.getPerson().getId())
                .senderUsername(savedMsg.getPerson().getName())
                .content(savedMsg.getContent())
                .createdAt(savedMsg.getCreatedAt())
                .build();
    }
}
