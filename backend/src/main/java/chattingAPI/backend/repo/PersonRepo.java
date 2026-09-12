package chattingAPI.backend.repo;

import chattingAPI.backend.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<Person,String> {
    Optional<Person> findByName(String name);
    boolean existsByName(String name);
    boolean existsByMail(String mail);

}
