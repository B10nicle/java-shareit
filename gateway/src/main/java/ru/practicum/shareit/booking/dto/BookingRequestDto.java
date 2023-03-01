package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

/**
 * @author Oleg Khilko
 */

@Data
@AllArgsConstructor
public class BookingRequestDto {
    private Long itemId;
    @FutureOrPresent(message = "Incorrect start date of booking")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime start;
    @Future(message = "Incorrect end date of booking")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime end;
}
