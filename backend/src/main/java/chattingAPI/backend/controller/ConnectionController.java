package chattingAPI.backend.controller;

import chattingAPI.backend.DTO.ConnectionResponse;
import chattingAPI.backend.DTO.GroupJoinRequest;
import chattingAPI.backend.DTO.PersonalChatResponse;
import chattingAPI.backend.DTO.SendPersonalRequest;
import chattingAPI.backend.Service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "${app.frontend.url}")
@RequestMapping("/api/v2/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    // 1. Personal connection request bhejo
    @PostMapping("/personal")
    public ResponseEntity<ConnectionResponse> sendPersonalRequest(
            @Valid @RequestBody SendPersonalRequest request,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.sendPersonalRequest(request, currentUsername));
    }

    // 2. Mere aaye hue pending personal requests
    @GetMapping("/personal/pending")
    public ResponseEntity<List<ConnectionResponse>> getPendingIncoming( @Valid Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.getPendingIncoming(currentUsername));
    }

    // 3. Maine bheji hue pending requests
    @GetMapping("/personal/sent")
    public ResponseEntity<List<ConnectionResponse>> getSentPending(Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.getSentPending(currentUsername));
    }

    // 4. Request accept karo (personal ya group dono)
    @PostMapping("/{id}/accept")
    public ResponseEntity<ConnectionResponse> acceptConnection(
            @PathVariable String id,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.acceptConnection(id, currentUsername));
    }

    // 5. Request reject karo
    @PostMapping("/{id}/reject")
    public ResponseEntity<ConnectionResponse> rejectConnection(
            @PathVariable String id,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.rejectConnection(id, currentUsername));
    }

    // 6. Group join request bhejo
    @PostMapping("/group/join")
    public ResponseEntity<ConnectionResponse> sendGroupJoinRequest(
            @Valid @RequestBody GroupJoinRequest request,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.sendGroupJoinRequest(request, currentUsername));
    }

    // 7. Group ke pending requests (sirf admin)
    @GetMapping("/group/{roomId}/requests")
    public ResponseEntity<List<ConnectionResponse>> getGroupPendingRequests(
            @PathVariable String roomId,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(connectionService.getGroupPendingRequests(roomId, currentUsername));
    }

    //method for fetch list of connection of personal room that request are accepted
     // for list them on frontend
    @GetMapping("/personal/allchat")
    public ResponseEntity<List<PersonalChatResponse>> getAllpersonalChat(Authentication authentication){
        String username=authentication.getName();
        return ResponseEntity.ok(connectionService.getAllPersonalChats(username));
    }
}

