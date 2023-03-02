package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Page<ItemRequest> findItemRequestByRequester_IdIsNotOrderByCreatedDesc(Long userId, Pageable pageable);

    List<ItemRequest> findItemRequestByRequester_IdIsNotOrderByCreatedDesc(Long userId);

    List<ItemRequest> findItemRequestByRequesterOrderByCreatedDesc(User user);
}
