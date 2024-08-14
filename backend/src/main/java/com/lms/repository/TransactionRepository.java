package com.lms.repository;

import com.lms.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByMemberMemberIdOrderByBorrowDateDesc(Integer memberId);

    List<Transaction> findByBookIsbnAndReturnDateIsNull(String isbn);

    @Query("SELECT t FROM Transaction t WHERE t.returnDate IS NULL AND t.dueDate < :date")
    List<Transaction> findOverdueTransactions(@Param("date") LocalDate date);

    @Query("SELECT t FROM Transaction t WHERE t.returnDate IS NULL AND t.dueDate < :date AND t.member.email IS NOT NULL")
    List<Transaction> findOverdueWithEmail(@Param("date") LocalDate date);

    @Query("SELECT t.book.isbn FROM Transaction t WHERE t.member.memberId = :memberId AND t.returnDate IS NULL")
    List<String> findCurrentlyBorrowedIsbns(@Param("memberId") Integer memberId);

    long countByMemberMemberIdAndReturnDateIsNull(Integer memberId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.returnDate IS NULL")
    long countActiveLoans();

    @Query("SELECT COALESCE(SUM(t.fine), 0) FROM Transaction t WHERE t.finePaid = false AND t.fine > 0")
    double totalUnpaidFines();
}
