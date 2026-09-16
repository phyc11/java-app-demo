package com.example.notification.repository;

import com.example.notification.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {long deleteByProcessedAtBefore(java.time.LocalDateTime cutoff);}
