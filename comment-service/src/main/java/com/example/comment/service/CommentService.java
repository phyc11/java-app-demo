package com.example.comment.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.comment.dto.CommentDTO;
import com.example.comment.model.Comment;
import com.example.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> getTaskCommentTree(Long taskId) {
        requirePositiveId(taskId, "Task ID");
        List<Comment> allComments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        Map<Long, CommentDTO> dtoMap = new LinkedHashMap<>();
        List<CommentDTO> rootComments = new ArrayList<>();

        for (Comment c : allComments) {
            CommentDTO dto = new CommentDTO(c);
            dtoMap.put(dto.getId(), dto);
        }

        for (Comment c : allComments) {
            CommentDTO dto = dtoMap.get(c.getId());
            if (c.getParentId() == null) {
                rootComments.add(dto);
            } else {
                CommentDTO parentDto = dtoMap.get(c.getParentId());
                if (parentDto != null) {
                    parentDto.getReplies().add(dto);
                } else {
                    rootComments.add(dto);
                }
            }
        }

        return rootComments;
    }

    public CommentDTO getComment(Long id) {
        return new CommentDTO(findComment(id));
    }

    @Transactional
    public CommentDTO addComment(CommentDTO dto, String author, String authorAvatarColor) {
        if (dto == null) {
            throw new IllegalArgumentException("Comment body is required");
        }
        requirePositiveId(dto.getTaskId(), "Task ID");
        String content = validateContent(dto.getContent());
        if (dto.getParentId() != null) {
            Comment parent = findComment(dto.getParentId());
            if (!dto.getTaskId().equals(parent.getTaskId())) {
                throw new IllegalArgumentException("Parent comment belongs to another task");
            }
        }
        Comment comment = new Comment(
                dto.getTaskId(),
                normalizeAuthor(author),
                normalizeColor(authorAvatarColor),
                content,
                dto.getParentId()
        );

        Comment saved = commentRepository.save(comment);
        return new CommentDTO(saved);
    }

    @Transactional
    public CommentDTO updateComment(Long id, String content, String author) {
        Comment comment = findComment(id);

        assertAuthor(comment, author, "edit");

        comment.setContent(validateContent(content));
        Comment updated = commentRepository.save(comment);
        return new CommentDTO(updated);
    }

    @Transactional
    public void deleteComment(Long id, String author) {
        Comment comment = findComment(id);
        assertAuthor(comment, author, "delete");
        deleteTree(comment, new HashSet<>());
    }

    public long getTaskCommentCount(Long taskId) {
        requirePositiveId(taskId, "Task ID");
        return commentRepository.countByTaskId(taskId);
    }

    private Comment findComment(Long id) {
        requirePositiveId(id, "Comment ID");
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
    }

    private void deleteTree(Comment comment, Set<Long> visited) {
        if (!visited.add(comment.getId())) {
            throw new IllegalStateException("Circular comment hierarchy detected");
        }
        for (Comment reply : commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId())) {
            deleteTree(reply, visited);
        }
        commentRepository.delete(comment);
    }

    private void assertAuthor(Comment comment, String author, String operation) {
        String normalizedAuthor = normalizeAuthor(author);
        if (!comment.getAuthor().equalsIgnoreCase(normalizedAuthor)) {
            throw new IllegalArgumentException("Only the comment author can " + operation + " this comment");
        }
    }

    private String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }
        String normalized = content.trim();
        if (normalized.length() > 4000) {
            throw new IllegalArgumentException("Comment content must not exceed 4000 characters");
        }
        return normalized;
    }

    private String normalizeAuthor(String author) {
        return author == null || author.trim().isEmpty() ? "Anonymous" : author.trim();
    }

    private String normalizeColor(String color) {
        if (color == null || !color.matches("^#[0-9a-fA-F]{6}$")) {
            return "#6366f1";
        }
        return color.toLowerCase(Locale.ROOT);
    }

    private void requirePositiveId(Long id, String field) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(field + " must be a positive number");
        }
    }
}
