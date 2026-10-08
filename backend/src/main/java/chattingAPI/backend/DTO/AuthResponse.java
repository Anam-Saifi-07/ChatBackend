package chattingAPI.backend.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String id;
    private String name;
    private String gmail;
    private String token;
    private boolean isMale;
    private Integer age;
    private  String phoneNo;
}
