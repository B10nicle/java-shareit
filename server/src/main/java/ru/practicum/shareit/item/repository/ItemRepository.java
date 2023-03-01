package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemRepository extends JpaRepository<Item, Long> {
    String searchQuery = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%')))";

    Page<Item> findAllByOwner_IdIs(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);

    @Query(searchQuery)
    Page<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequest_IdIs(Long requestId);

    List<Item> findAllByOwner_IdIs(Long ownerId);

    @Query(searchQuery)
    List<Item> search(String text);
}
