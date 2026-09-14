package com.example.comment.service;

import com.example.comment.dto.CommentDTO;
import com.example.comment.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceIntegrationTest {
    @Autowired private CommentService commentService;
    @Autowired private CommentRepository commentRepository;

    @BeforeEach
    void cleanDatabase() {
        commentRepository.deleteAll();
    }

    @Test
    void emptyTaskDoesNotCreateSeedComments() {
        assertTrue(commentService.getTaskCommentTree(1L).isEmpty());
        assertEquals(0, commentRepository.count());
    }

    @Test
    void createsNestedCommentTree() {
        CommentDTO root = createComment(1L, null, "Root", "alice");
        CommentDTO reply = createComment(1L, root.getId(), "Reply", "bob");
        createComment(1L, reply.getId(), "Nested reply", "carol");

        List<CommentDTO> tree = commentService.getTaskCommentTree(1L);

        assertEquals(1, tree.size());
        assertEquals(1, tree.get(0).getReplies().size());
        assertEquals(1, tree.get(0).getReplies().get(0).getReplies().size());
        assertEquals(3, commentService.getTaskCommentCount(1L));
    }

    @Test
    void rejectsReplyToCommentFromAnotherTask() {
        CommentDTO parent = createComment(1L, null, "Task one", "alice");
        CommentDTO request = request(2L, parent.getId(), "Wrong task");

        assertThrows(IllegalArgumentException.class,
                () -> commentService.addComment(request, "bob", null));
    }

    @Test
    void onlyAuthorCanEditOrDeleteComment() {
        CommentDTO saved = createComment(1L, null, "Original", "alice");

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(saved.getId(), "Changed", "bob"));
        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(saved.getId(), "bob"));

        CommentDTO updated = commentService.updateComment(saved.getId(), " Changed ", "ALICE");
        assertEquals("Changed", updated.getContent());
    }

    @Test
    void deletingRootDeletesAllNestedReplies() {
        CommentDTO root = createComment(1L, null, "Root", "alice");
        CommentDTO reply = createComment(1L, root.getId(), "Reply", "bob");
        createComment(1L, reply.getId(), "Nested", "carol");

        commentService.deleteComment(root.getId(), "alice");

        assertEquals(0, commentRepository.count());
    }

    @Test
    void validatesContentAndAvatarColor() {
        assertThrows(IllegalArgumentException.class,
                () -> commentService.addComment(request(1L, null, "   "), "alice", null));

        CommentDTO saved = commentService.addComment(request(1L, null, "Valid"), "alice", "not-a-color");
        assertEquals("#6366f1", saved.getAuthorAvatarColor());
    }

    private CommentDTO createComment(Long taskId, Long parentId, String content, String author) {
        return commentService.addComment(request(taskId, parentId, content), author, "#112233");
    }

    private CommentDTO request(Long taskId, Long parentId, String content) {
        CommentDTO request = new CommentDTO();
        request.setTaskId(taskId);
        request.setParentId(parentId);
        request.setContent(content);
        return request;
    }
}
