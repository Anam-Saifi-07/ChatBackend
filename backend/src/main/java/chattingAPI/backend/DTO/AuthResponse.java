package chattingAPI.backend.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String id;
    private String name;
    private String mail;
    private String token;
    private boolean isMale;
    private Integer age;
    private  String phoneNo;
}
