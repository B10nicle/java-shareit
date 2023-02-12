package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;
import org.hibernate.Hibernate;
import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;
import java.util.Objects;

import static javax.persistence.GenerationType.*;

/**
 * @author Oleg Khilko
 */

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemRequest that = (ItemRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
