package chattingAPI.backend.controller;

import chattingAPI.backend.DTO.MessageResponse;
import chattingAPI.backend.DTO.SendMessagePayload;
import chattingAPI.backend.Service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate; // <-- 1. STOMP Broadcast Tool
    private final ChatService chatService;

    public ChatSocketController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload SendMessagePayload payload, Principal principal) {
        String currentUsername = principal.getName();

        // Database me save & format kiya
        MessageResponse response = chatService.saveAndFormatMessage(payload, currentUsername);

        // Target Topic Destination banaya
        String destination = "/topic/room." + payload.getRoomId() + "/messages";

        // =========================================================================
        // THIS IS THE CODE THAT SENDS THE MESSAGE TO ALL SUBSCRIBERS:
        // =========================================================================
        messagingTemplate.convertAndSend(destination, response);
    }
}
