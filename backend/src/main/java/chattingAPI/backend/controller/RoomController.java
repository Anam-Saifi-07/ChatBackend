package chattingAPI.backend.controller;
import chattingAPI.backend.DTO.RoomDto;
import chattingAPI.backend.entity.Message;
import chattingAPI.backend.entity.Room;
import chattingAPI.backend.repo.MessageRepo;
import chattingAPI.backend.repo.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "${app.frontend.url}")
@RequestMapping("/api")
public class RoomController {

    private final RoomRepo roomRepo;
    private  final MessageRepo messageRepo;
// check for ping pong process
    @GetMapping("/")
    public ResponseEntity<?> home() {
           return ResponseEntity.ok("Backend is running!");
    }

//    for join the room by the id
    @GetMapping("/get/{id}")
    public ResponseEntity<?> joinRoom(@PathVariable String id){
        Optional<Room> room=roomRepo.findById(id);
        if(room.isEmpty()){
            return ResponseEntity.badRequest().body("Room not Exist");
        }
        return ResponseEntity.ok(room);
    }

//    for make the new room by the id
    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody RoomDto roomDto){
        Optional<Room> room=roomRepo.findById(roomDto.getId());
        if(!room.isEmpty()){
            return ResponseEntity.badRequest().body("Room Already Exit");
        }
        Room newRoom=new Room();
        newRoom.setId(roomDto.getId());
        newRoom.setName(roomDto.getName());
        roomRepo.save(newRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRoom);
    }

//    get all message of group of id
    @GetMapping("allMsg/{id}")
    public ResponseEntity<?> getAllMsg(@PathVariable  String id){
        Optional<Room> room=roomRepo.findById(id);
        if(room.isEmpty()){
            return ResponseEntity.badRequest().body("Room Not Exist...");
        }
        List<Message> allmsg=messageRepo.findAllById(123L);
        if(allmsg.isEmpty())return ResponseEntity.badRequest().body("Hard coded");
        return ResponseEntity.ok(allmsg);
    }

}
