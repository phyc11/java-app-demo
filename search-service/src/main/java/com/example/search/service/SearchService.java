package com.example.search.service;

import com.example.search.dto.SearchRequestDto;
import com.example.search.model.TaskDocument;
import com.example.search.repository.TaskSearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchService {

    private final TaskSearchRepository taskSearchRepository;

    public SearchService(TaskSearchRepository taskSearchRepository) {
        this.taskSearchRepository = taskSearchRepository;
    }

    @Transactional
    public TaskDocument indexTask(TaskDocument taskDocument) {
        if (taskDocument.getUpdatedAt() == null) {
            taskDocument.setUpdatedAt(LocalDateTime.now());
        }
        if (taskDocument.getCreatedAt() == null) {
            taskDocument.setCreatedAt(LocalDateTime.now());
        }
        return taskSearchRepository.save(taskDocument);
    }

    @Transactional
    public List<TaskDocument> indexBatch(List<TaskDocument> taskDocuments) {
        return taskSearchRepository.saveAll(taskDocuments);
    }

    public List<TaskDocument> search(SearchRequestDto request) {
        return taskSearchRepository.searchTasks(
                request.getQuery(),
                request.getStatus(),
                request.getPriority(),
                request.getCategoryName(),
                request.getAssigneeName()
        );
    }

    public List<String> getSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return taskSearchRepository.findSuggestions(query.trim());
    }

    @Transactional
    public void deleteIndex(Long id) {
        taskSearchRepository.deleteById(id);
    }
}
