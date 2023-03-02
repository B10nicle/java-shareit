package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author Oleg Khilko
 */

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;
    @NotBlank(message = "Description cannot be null")
    private String description;
    private Integer requestorId;
    private LocalDateTime created;

}