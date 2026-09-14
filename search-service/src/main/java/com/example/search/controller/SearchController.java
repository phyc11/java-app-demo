package com.example.search.controller;

import com.example.common.dto.ApiResponse;
import com.example.search.dto.SearchRequestDto;
import com.example.search.model.TaskDocument;
import com.example.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskDocument>>> searchTasks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String assigneeName) {

        SearchRequestDto request = new SearchRequestDto();
        request.setQuery(query);
        request.setStatus(status);
        request.setPriority(priority);
        request.setCategoryName(categoryName);
        request.setAssigneeName(assigneeName);

        List<TaskDocument> results = searchService.search(request);
        return ResponseEntity.ok(ApiResponse.ok("Found " + results.size() + " tasks matching criteria", results));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(@RequestParam String query) {
        List<String> suggestions = searchService.getSuggestions(query);
        return ResponseEntity.ok(ApiResponse.ok("Search suggestions retrieved", suggestions));
    }

    @PostMapping("/index")
    public ResponseEntity<ApiResponse<TaskDocument>> indexTask(@RequestBody TaskDocument taskDocument) {
        TaskDocument indexed = searchService.indexTask(taskDocument);
        return ResponseEntity.ok(ApiResponse.ok("Task indexed successfully", indexed));
    }

    @PostMapping("/index/batch")
    public ResponseEntity<ApiResponse<List<TaskDocument>>> indexBatch(@RequestBody List<TaskDocument> taskDocuments) {
        List<TaskDocument> indexed = searchService.indexBatch(taskDocuments);
        return ResponseEntity.ok(ApiResponse.ok(indexed.size() + " tasks batch indexed successfully", indexed));
    }

    @DeleteMapping("/index/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIndex(@PathVariable Long id) {
        searchService.deleteIndex(id);
        return ResponseEntity.ok(ApiResponse.ok("Task index removed successfully", null));
    }
}
