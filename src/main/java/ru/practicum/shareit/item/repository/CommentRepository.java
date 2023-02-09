package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentByItem_IdIsOrderByCreated(Long itemId);
}
