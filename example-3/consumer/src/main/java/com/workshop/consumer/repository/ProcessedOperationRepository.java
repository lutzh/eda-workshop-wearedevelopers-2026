package com.workshop.consumer.repository;

import com.workshop.consumer.entity.ProcessedOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedOperationRepository extends JpaRepository<ProcessedOperation, Long> {
}
