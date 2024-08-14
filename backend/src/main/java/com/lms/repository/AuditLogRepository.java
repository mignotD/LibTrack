package com.lms.repository;

import com.lms.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
    long count();
}
