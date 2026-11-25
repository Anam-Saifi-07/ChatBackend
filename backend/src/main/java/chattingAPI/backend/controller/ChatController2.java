package chattingAPI.backend.controller;




import chattingAPI.backend.DTO.CreateRoomRequest;
import chattingAPI.backend.DTO.MessageResponse;
import chattingAPI.backend.DTO.RoomResponse;
import chattingAPI.backend.Service.ChatService;
import chattingAPI.backend.entity.Person;
import chattingAPI.backend.entity.Room;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin(origins = "${app.frontend.url}")
@RequestMapping("/api/v2/rooms")
public class ChatController2 {

    private final ChatService chatService;

    public ChatController2(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ping/pong")
    public ResponseEntity<?> pingpong() {
        System.out.println("Backend is running");
        return ResponseEntity.ok("Backend is running");

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
    @GetMapping("/allGroupsExceptCurrent")
    public ResponseEntity<List<RoomResponse>> getAllGroupsExceptCurrentUserNotMemberOfThem(Authentication authentication){
        //fetch all rooms that are group not individual chatting
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(chatService.getAllGroupsExceptCurrent(currentUsername));
    }
    @GetMapping("/allGroupsOfCurrentUser")
    public ResponseEntity<List<RoomResponse>>getAllGroupsOfCurrentUser(Authentication authentication){
        String currentUsername= authentication.getName();
        return ResponseEntity.ok(chatService.allGroupsOfCurrentUser(currentUsername));
    }
    @GetMapping("/exploreAllNewPerson")
    public ResponseEntity<List<Person>> getExploreAllNewPersonForChatting(Authentication authentication){
        //fetch all rooms that are group not individual chatting
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(chatService.exploreAllNewPersonForPersonalChatting (currentUsername));
    }

    @GetMapping("/getAllPersonThatInChatting")
    public ResponseEntity<List<Person>> getAllPersonThatChatting(Authentication authentication){
        //fetch all rooms that are group not individual chatting
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(chatService.getAllPersonThatAlreadyinChatting (currentUsername));
    }

    @GetMapping("/{otherId}/getRoomOfCurrentAndOther") //getRoomId of current user personal chatted with other user
    public ResponseEntity<Room> getRoomOfPersonalChat(Authentication authentication, @PathVariable String otherId){
        String currentUsername= authentication.getName();
        return ResponseEntity.ok(chatService.roomOfPersonsalChat(currentUsername,otherId));
    }

    // 3. Fetch Message History of a specific Room (With Pagination)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            Authentication authentication) {

        // Pass authentication.getName() to ensure caller is a member
        return ResponseEntity.ok(chatService.getRoomMessages(roomId, authentication.getName(), page, size));
    }
}
