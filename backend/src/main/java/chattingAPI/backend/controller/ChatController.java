package chattingAPI.backend.controller;

import chattingAPI.backend.AppConstantVaribles;
import chattingAPI.backend.entity.Message;
import chattingAPI.backend.entity.Room;
import chattingAPI.backend.payload.RequestMsgBody;
import chattingAPI.backend.repo.MessageRepo;
import chattingAPI.backend.repo.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@CrossOrigin(AppConstantVaribles.FRONT_END_URL)
public class ChatController {

    private final RoomRepo roomRepo;

    private final MessageRepo messageRepo;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/msg/{roomId}")
    public Message handleMessage(@Payload RequestMsgBody requestmsgbody, @DestinationVariable String roomId){
        Optional<Room>  room= roomRepo.findById(roomId);
        if(room.isEmpty()){
            throw new RuntimeException("Room not exist");
        }
        System.out.print(requestmsgbody.getContent());
        System.out.print(requestmsgbody.getPersonID());
        Message newMessage=new Message();
        newMessage.setContent(requestmsgbody.getContent());
        newMessage.setPersonID(requestmsgbody.getPersonID());
        newMessage.setRoomID(roomId);
        messageRepo.save(newMessage);
        return newMessage;
    }
}
