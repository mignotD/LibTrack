package com.lms.repository;

import com.lms.entity.BorrowingLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowingLimitRepository extends JpaRepository<BorrowingLimit, Integer> {
    Optional<BorrowingLimit> findByMemberTypeIgnoreCase(String memberType);
}
