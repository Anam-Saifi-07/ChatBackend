package chattingAPI.backend.Security;

import chattingAPI.backend.DTO.AuthResponse;
import chattingAPI.backend.DTO.LoginRequest;
import chattingAPI.backend.DTO.RegisterRequest;
import chattingAPI.backend.entity.Person;
import chattingAPI.backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private  final PersonRepo personRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public AuthService(JwtTokenProvider jwtTokenProvider,PersonRepo personRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider=jwtTokenProvider;
    }

    public AuthResponse register(RegisterRequest request) {
        if (personRepo.existsByName(request.getName())) {
            throw new RuntimeException("Username already taken!");
        }
        if (personRepo.existsByMail(request.getMail())) {
            throw new RuntimeException("Email already registered!");
        }

        Person user = Person.builder()
                .name(request.getName())
                .mail(request.getMail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        personRepo.save(user);


        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(user.getId(), user.getName(), user.getMail(),token);
    }
    //here
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword())
        );

        Person user = personRepo.findByName(request.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(user.getId(), user.getName(), user.getMail(),token);
    }
}
