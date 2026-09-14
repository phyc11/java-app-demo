package com.example.comment.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.comment.dto.CommentDTO;
import com.example.comment.model.Comment;
import com.example.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> getTaskCommentTree(Long taskId) {
        List<Comment> allComments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        if (allComments.isEmpty()) {
            createInitialSeedComments(taskId);
            allComments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        }

        Map<Long, CommentDTO> dtoMap = new HashMap<>();
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

    public CommentDTO addComment(CommentDTO dto, String author, String authorAvatarColor) {
        Comment comment = new Comment(
                dto.getTaskId(),
                author != null ? author : "Anonymous",
                authorAvatarColor != null ? authorAvatarColor : "#6366f1",
                dto.getContent(),
                dto.getParentId()
        );

        Comment saved = commentRepository.save(comment);
        return new CommentDTO(saved);
    }

    public CommentDTO updateComment(Long id, String content, String author) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        if (!comment.getAuthor().equalsIgnoreCase(author)) {
            throw new IllegalArgumentException("Ban khong co quyen sua binh luan cua nguoi khac!");
        }

        comment.setContent(content);
        Comment updated = commentRepository.save(comment);
        return new CommentDTO(updated);
    }

    public void deleteComment(Long id, String author) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        if (!comment.getAuthor().equalsIgnoreCase(author)) {
            throw new IllegalArgumentException("Ban khong co quyen xoa binh luan cua nguoi khac!");
        }

        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(id);
        commentRepository.deleteAll(replies);
        commentRepository.delete(comment);
    }

    public long getTaskCommentCount(Long taskId) {
        return commentRepository.countByTaskId(taskId);
    }

    private void createInitialSeedComments(Long taskId) {
        Comment c1 = commentRepository.save(new Comment(
                taskId,
                "Admin",
                "#6366f1",
                "Task nay can hoan thien API Gateway routing truoc khi ban giao.",
                null
        ));

        commentRepository.save(new Comment(
                taskId,
                "Developer A",
                "#ec4899",
                "Da kiem tra! API Gateway da san sang proxy requests xuong comment-service.",
                c1.getId()
        ));
    }
}
