package chattingAPI.backend.Security;

import chattingAPI.backend.entity.Person;
import chattingAPI.backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private  PersonRepo personRepo;
    //fetch user by person id

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person=personRepo.findByName(username).orElseThrow(()->new UsernameNotFoundException("User not found by the id"));
        return new org.springframework.security.core.userdetails.User(
                person.getName(),
                person.getPassword(),
                new ArrayList<>()
        );
    }
}
