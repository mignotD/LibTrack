package com.lms.repository;

import com.lms.entity.BookRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Integer> {
    List<BookRequest> findByMemberMemberIdOrderByRequestDateDesc(Integer memberId);
    List<BookRequest> findByStatusOrderByRequestDateDesc(String status);
    List<BookRequest> findAllByOrderByRequestDateDesc();
    long countByStatus(String status);
}
