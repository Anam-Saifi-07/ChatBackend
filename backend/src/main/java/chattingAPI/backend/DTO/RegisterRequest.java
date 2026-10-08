package chattingAPI.backend.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String mail;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private boolean isMale;
    private  Integer age;
    private String phoneNo;

}
