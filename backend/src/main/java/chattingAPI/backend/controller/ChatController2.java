package chattingAPI.backend.controller;




import chattingAPI.backend.DTO.CreateRoomRequest;
import chattingAPI.backend.DTO.MessageResponse;
import chattingAPI.backend.DTO.RoomResponse;
import chattingAPI.backend.Service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/rooms")
public class ChatController2 {

    private final ChatService chatService;

    public ChatController2(ChatService chatService) {
        this.chatService = chatService;
    }

    // 1. New Chat Room / Direct Chat Create EndPoint
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request,
                                                   Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(chatService.createRoom(request, currentUsername));
    }

    // 2. Fetch All Rooms for Logged-In User
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getUserRooms(Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(chatService.getUserRooms(currentUsername));
    }

    // 3. Fetch Message History of a specific Room (With Pagination)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(chatService.getRoomMessages(roomId, page, size));
    }
}
