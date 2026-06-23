package chattingAPI.backend.config;

import chattingAPI.backend.AppConstantVaribles;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@CrossOrigin(AppConstantVaribles.FRONT_END_URL)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
//        /topic/..../.../... per server message ko send kar diyga

        config.setApplicationDestinationPrefixes("/app");
//        set karta hai prefix jinko server side controller ko handle karni huti hai
//        "/app/chat" matlab user /app/chat par message send karta hai aur /chat ek websocket endpoint hai
//        jiske liye controller me @MessageingRequest("/chat") huga
//        @MessageRequest like @PostMapping but for WebSocket Full duplex Connection
//        simply --> server send msg on /topic/..../...../
//                   client send msg on app/..../....
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOrigins(AppConstantVaribles.FRONT_END_URL) //this for frontend url
                .withSockJS();
    }
//    /chat par connetion banega
}
