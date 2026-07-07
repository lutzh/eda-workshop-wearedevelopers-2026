package com.workshop.consumer.repository;

import com.workshop.consumer.entity.ProcessedMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageIdRepository extends JpaRepository<ProcessedMessageId, String> {
}
