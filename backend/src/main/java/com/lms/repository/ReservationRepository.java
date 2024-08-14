package com.lms.repository;

import com.lms.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByMemberMemberId(Integer memberId);
    List<Reservation> findByBookIsbnAndFulfilledFalse(String isbn);
    Optional<Reservation> findByBookIsbnAndMemberMemberIdAndFulfilledFalse(String isbn, Integer memberId);
    boolean existsByBookIsbnAndMemberMemberIdAndFulfilledFalse(String isbn, Integer memberId);
    List<Reservation> findByFulfilledFalseAndBookAvailableGreaterThan(Integer available);
}
