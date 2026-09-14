package com.example.comment.repository;

import com.example.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
    long countByTaskId(Long taskId);
}
