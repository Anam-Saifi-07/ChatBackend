package chattingAPI.backend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


public class AppConstantVaribles {
     @Value("${app.frontend.url}")
     public static final String FRONT_END_URL;

}
