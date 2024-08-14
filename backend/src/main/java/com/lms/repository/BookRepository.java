package com.lms.repository;

import com.lms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenreContainingIgnoreCase(String genre);
    List<Book> findByIsbnContainingIgnoreCase(String isbn);

    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
           "(:isbn IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')))")
    List<Book> searchAdvanced(@Param("title") String title, @Param("author") String author,
                              @Param("genre") String genre, @Param("isbn") String isbn);

    @Query("SELECT DISTINCT b.genre FROM Book b WHERE b.genre IS NOT NULL AND b.genre <> '' ORDER BY b.genre")
    List<String> findDistinctGenres();

    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b.isbn ORDER BY COALESCE(AVG(r.rating), 0) DESC")
    List<Book> findPopularBooks();

    List<Book> findByStockGreaterThan(Integer stock);

    @Query("SELECT b FROM Book b WHERE b.available > 0 AND b.isbn NOT IN " +
           "(SELECT w.book.isbn FROM Wishlist w WHERE w.member.memberId = :memberId)")
    List<Book> findAvailableNotInWishlist(@Param("memberId") Integer memberId);
}
