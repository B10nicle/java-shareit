package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.domain.Sort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;
import java.util.List;

import static ru.practicum.shareit.booking.enums.BookingTimeState.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static ru.practicum.shareit.utils.Pagination.*;
import static java.util.stream.Collectors.*;
import static java.time.LocalDateTime.*;
import static java.util.Collections.*;
import static java.util.Comparator.*;

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
    public ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Long userId) {
        validate(itemDto);
        var user = mapToUser(userService.get(userId));
        var item = mapToItem(itemDto);
        item.setOwner(user);
        if (itemRequestDto != null)
            item.setRequest(ItemRequestMapper.mapToItemRequest(
                    itemRequestDto, userService.get(itemRequestDto.getRequesterId())));
        var save = itemRepository.save(item);
        return mapToItemDto(save);
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
        return mapToItemDto(save);
    }

    @Override
    public ItemAllFieldsDto get(Long id, Long userId) {
        var item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id#" + id + " does not exist"));
        var comments = getAllComments(id);
        var bookings = bookingService.getBookingsByItem(item.getId(), userId);
        return mapToItemAllFieldsDto(item,
                getLastItem(bookings),
                getNextItem(bookings),
                comments);
    }

    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemAllFieldsDto> getAllItems(Long userId, Integer from, Integer size) {
        Stream<Item> stream;
        if (userId == null) throw new ValidationException("User ID cannot be null");
        var bookings = bookingService.getBookingsByOwnerId(userId, null)
                .stream()
                .collect(groupingBy((BookingAllFieldsDto bookingAllFieldsDto) -> bookingAllFieldsDto.getItem().getId()));
        var comments = getAllComments().stream()
                .collect(groupingBy(CommentDto::getItemId));
        var pageRequest = makePageRequest(from, size, Sort.by("id").ascending());
        if (pageRequest == null)
            stream = itemRepository.findAllByOwner_IdIs(userId).stream();
        else
            stream = itemRepository.findAllByOwner_IdIs(userId, pageRequest).stream();
        return stream.map(item -> ItemMapper.mapToItemAllFieldsDto(item,
                        getLastItem(bookings.get(item.getId())),
                        getNextItem(bookings.get(item.getId())),
                        comments.get(item.getId())))
                .collect(toList());
    }

    @Override
    public List<ItemDto> search(String text, Long userId, Integer from, Integer size) {
        Stream<Item> stream;
        if (text.isBlank()) return emptyList();
        var pageRequest = makePageRequest(from, size, Sort.by("id").ascending());
        if (pageRequest == null)
            stream = itemRepository.search(text).stream();
        else
            stream = itemRepository.search(text, pageRequest).stream();
        return stream
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDto saveComment(CommentDto commentDto,
                                  Long itemId,
                                  Long userId) {
        if (commentDto.getText() == null || commentDto.getText().isBlank())
            throw new ValidationException("Comment text cannot be blank");
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id#" + itemId + " does not exist"));
        var user = mapToUser(userService.get(userId));
        var bookings = bookingService.getAllBookings(userId, PAST.name());
        if (bookings.isEmpty()) throw new ValidationException("User cannot make comments");
        var comment = mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now());
        var save = commentRepository.save(comment);
        return mapToCommentDto(save);
    }

    @Override
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(toList());
    }

    @Override
    public List<CommentDto> getAllComments(Long itemId) {
        return commentRepository.findCommentByItem_IdIsOrderByCreated(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemRepository.findAllByRequest_IdIs(requestId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIn(requests)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    private BookingAllFieldsDto getNextItem(List<BookingAllFieldsDto> bookings) {
        if (bookings != null)
            return bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now()))
                    .min(comparing(BookingAllFieldsDto::getEnd))
                    .orElse(null);
        else
            return null;
    }

    private BookingAllFieldsDto getLastItem(List<BookingAllFieldsDto> bookings) {
        if (bookings != null)
            return bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now()))
                    .max(comparing(BookingAllFieldsDto::getEnd))
                    .orElse(null);
        else
            return null;
    }

    private void validate(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Name cannot be blank");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("Description cannot be blank");
        if (item.getAvailable() == null)
            throw new ValidationException("Available cannot be null");
    }
}
