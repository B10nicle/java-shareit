package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.validation.Created;
import ru.practicum.shareit.user.validation.Updated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author Oleg Khilko
 */

@Data
public class UserDto {
    private Integer id;
    @NotBlank(groups = Created.class, message = "Name cannot be blank")
    private String name;
    @Email(groups = {Updated.class, Created.class}, message = "Email is incorrect")
    @NotEmpty(groups = Created.class, message = "Email cannot be empty")
    private String email;
}
