package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.validation.Created;
import ru.practicum.shareit.user.validation.Updated;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Email;

/**
 * @author Oleg Khilko
 */

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = Created.class, message = "Name cannot be blank")
    private String name;
    @Email(groups = {Updated.class, Created.class}, message = "Email is incorrect")
    @NotEmpty(groups = Created.class, message = "Email cannot be empty")
    private String email;
}
