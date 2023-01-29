package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.booking.enums.BookingTimeState.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static java.util.stream.Collectors.*;
import static java.util.Collections.*;

/**
 * @author Oleg Khilko
 */

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto save(ItemDto itemDto, Long userId) {
        validate(itemDto);
        var user = toUser(userService.get(userId));
        var item = toItem(itemDto);
        item.setOwner(user);
        var save = itemRepository.save(item);
        return toItemDto(save);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null");
        var item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new NotFoundException("Item with id#" + itemDto.getId() + " does not exist"));
        if (!item.getOwner().getId().equals(userId))
            throw new NotFoundException("Item has another user");
        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        var save = itemRepository.save(item);
        return toItemDto(save);
    }

    @Override
    public ItemAllFieldsDto get(Long id, Long userId) {
        var item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id#" + id + " does not exist"));
        var comments = getAllComments().stream()
                .collect(groupingBy(CommentDto::getItemId));
        var bookings = bookingService.getBookingsByItem(item.getId(), userId);
        return toItemAllFieldsDto(item,
                getLastItem(bookings),
                getNextItem(bookings),
                comments.get(item.getId()));
    }

    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemAllFieldsDto> getAllItems(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null");
        var comments = getAllComments().stream()
                .collect(groupingBy(CommentDto::getItemId));
        var bookings = bookingService.getBookingsByOwner(userId, null).stream()
                .collect(groupingBy((BookingAllFieldsDto bookingExtendedDto) -> bookingExtendedDto.getItem().getId()));
        return itemRepository.findAllByOwner_IdIs(userId).stream()
                .map(item -> getItemAllFieldsDto(comments, bookings, item))
                .collect(toList());
    }

    @Override
    public List<ItemDto> search(String text, Long userId) {
        if (text.isBlank()) return emptyList();
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto,
                                    Long itemId,
                                    Long userId) {
        if (commentDto.getText() == null || commentDto.getText().isBlank())
            throw new ValidationException("Comment text cannot be blank");
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id#" + itemId + " does not exist"));
        var user = toUser(userService.get(userId));
        var bookings = bookingService.getAll(userId, PAST.name());
        if (bookings.isEmpty()) throw new ValidationException("User cannot make comments");
        var comment = toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        var save = commentRepository.save(comment);
        return toCommentDto(save);
    }

    @Override
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
    }

    private ItemAllFieldsDto getItemAllFieldsDto(Map<Long, List<CommentDto>> comments,
                                                 Map<Long, List<BookingAllFieldsDto>> bookings,
                                                 Item item) {
        return toItemAllFieldsDto(item,
                getLastItem(bookings.get(item.getId())),
                getNextItem(bookings.get(item.getId())),
                comments.get(item.getId()));
    }

    private void validate(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Name cannot be blank");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("Description cannot be blank");
        if (item.getAvailable() == null)
            throw new ValidationException("Available cannot be null");
    }

    private BookingAllFieldsDto getNextItem(List<BookingAllFieldsDto> bookings) {
        return bookings != null
                ? bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingAllFieldsDto::getEnd)).orElse(null)
                : null;
    }

    private BookingAllFieldsDto getLastItem(List<BookingAllFieldsDto> bookings) {
        return bookings != null
                ? bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingAllFieldsDto::getEnd)).orElse(null)
                : null;
    }
}
