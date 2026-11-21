package chattingAPI.backend;

import chattingAPI.backend.Security.JwtTokenProvider;
import chattingAPI.backend.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Test
    void testGenerateAndValidateJwtToken() {
        Person person = Person.builder()
                .name("King Faisal")
                .mail("king@gmail.com")
                .age(17)
                .isMale(true)
                .password("ff48ggfr")
                .id("fr")
                .build();

        String token = jwtTokenProvider.generateToken(person);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isBlank(), "Token should not be blank");

        String username = jwtTokenProvider.getUsernameFromJwtToken(token);
        assertEquals("King Faisal", username, "Extracted username must match person name");

        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid, "Generated token should be valid");
    }
}
