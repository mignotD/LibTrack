package com.lms.repository;

import com.lms.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByMemberMemberId(Integer memberId);
    Optional<Wishlist> findByBookIsbnAndMemberMemberId(String isbn, Integer memberId);
    boolean existsByBookIsbnAndMemberMemberId(String isbn, Integer memberId);
    void deleteByBookIsbnAndMemberMemberId(String isbn, Integer memberId);
}
