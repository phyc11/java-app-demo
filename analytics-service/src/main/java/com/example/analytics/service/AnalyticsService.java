package com.example.analytics.service;

import com.example.analytics.dto.AnalyticsDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    public AnalyticsDTO getAnalytics() {
        Map<String, Long> categoryDist = new HashMap<>();
        categoryDist.put("Backend Development", 5L);
        categoryDist.put("Frontend UI/UX", 3L);
        categoryDist.put("DevOps & Deployment", 2L);

        Map<String, Long> statusDist = new HashMap<>();
        statusDist.put("TODO", 3L);
        statusDist.put("IN_PROGRESS", 3L);
        statusDist.put("COMPLETED", 4L);

        Map<String, Long> priorityDist = new HashMap<>();
        priorityDist.put("LOW", 2L);
        priorityDist.put("MEDIUM", 4L);
        priorityDist.put("HIGH", 3L);
        priorityDist.put("URGENT", 1L);

        long total = 10L;
        long completed = 4L;
        double completionRate = 40.0;

        return new AnalyticsDTO(total, completed, completionRate, categoryDist, statusDist, priorityDist);
    }
}
