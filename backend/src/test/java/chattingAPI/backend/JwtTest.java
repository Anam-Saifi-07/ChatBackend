package chattingAPI.backend;

import chattingAPI.backend.Security.JwtTokenProvider;
import chattingAPI.backend.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class JwtTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void method(){
        System.out.println("hello");

//        JwtTokenProvider jwtTokenProvider=new JwtTokenProvider();
        //person
        Person person = Person.builder()
                .name("King Faisal")
                .mail("king@gmail.com")
                .age(17)
                .isMale(true)
                .password("ff48ggfr")
                .id("fr")
                .build();
        String token=jwtTokenProvider.generateToken(person);
        System.out.println("jwt: "+token);
        System.out.println("Username: "+jwtTokenProvider.getUsernameFromJwtToken(token));

    }


}
