package chattingAPI.backend.service;

import chattingAPI.backend.DTO.MessageResponse;
import chattingAPI.backend.DTO.SendMessagePayload;
import chattingAPI.backend.Service.ChatService;
import chattingAPI.backend.entity.Message;
import chattingAPI.backend.entity.Person;
import chattingAPI.backend.entity.Room;
import chattingAPI.backend.repo.MessageRepo;
import chattingAPI.backend.repo.PersonRepo;
import chattingAPI.backend.repo.RoomMemberRepo;
import chattingAPI.backend.repo.RoomRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    // 1. Fake repositories that run in-memory without connecting to MySQL
    @Mock
    private PersonRepo userRepository;

    @Mock
    private RoomRepo chatRoomRepository;

    @Mock
    private RoomMemberRepo roomMemberRepository;

    @Mock
    private MessageRepo messageRepository;

    // 2. Inject fake repositories into the real ChatService
    @InjectMocks
    private ChatService chatService;

    private Person testUser;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testUser = Person.builder()
                .id("user-123")
                .name("Alex")
                .mail("alex@example.com")
                .build();

        testRoom = Room.builder()
                .id("room-456")
                .name("General Room")
                .build();
    }

    @Test
    void saveAndFormatMessage_Success() {
        // GIVEN: Set up test input and repository mock responses
        SendMessagePayload payload = new SendMessagePayload();
        payload.setRoomId("room-456");
        payload.setContent("Hello World!");

        when(userRepository.findByName("Alex")).thenReturn(Optional.of(testUser));
        when(chatRoomRepository.findById("room-456")).thenReturn(Optional.of(testRoom));
        when(roomMemberRepository.existsByPersonIdAndRoomId("user-123", "room-456")).thenReturn(true);

        Message savedMessage = Message.builder()
                .id(1L)
                .person(testUser)
                .room(testRoom)
                .content("Hello World!")
                .createdAt(LocalDateTime.now())
                .build();
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // WHEN: Calling the service method
        MessageResponse result = chatService.saveAndFormatMessage(payload, "Alex");

        // THEN: Verify the returned MessageResponse
        assertNotNull(result);
        assertEquals("Hello World!", result.getContent());
        assertEquals("Alex", result.getSenderUsername());
        assertEquals("room-456", result.getRoomId());

        // Verify message was saved to repository exactly once
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void saveAndFormatMessage_FailsWhenUserNotMember() {
        // GIVEN: User exists and room exists, but user is NOT a member of the room
        SendMessagePayload payload = new SendMessagePayload();
        payload.setRoomId("room-456");
        payload.setContent("Unauthorized message");

        when(userRepository.findByName("Alex")).thenReturn(Optional.of(testUser));
        when(chatRoomRepository.findById("room-456")).thenReturn(Optional.of(testRoom));
        when(roomMemberRepository.existsByPersonIdAndRoomId("user-123", "room-456")).thenReturn(false);

        // WHEN & THEN: Verify an exception is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatService.saveAndFormatMessage(payload, "Alex");
        });

        assertEquals("You are not a member of this room", exception.getMessage());

        // Verify message was never saved to the repository
        verify(messageRepository, never()).save(any(Message.class));
    }
}
