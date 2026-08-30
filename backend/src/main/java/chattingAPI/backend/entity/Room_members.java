package chattingAPI.backend.entity;

import chattingAPI.backend.config.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Room_members {
    @Id
    private long id;

    private Roles role;

    @CreationTimestamp
    private Date date;
}
