package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
