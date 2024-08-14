package com.lms.repository;

import com.lms.entity.OverdueNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OverdueNotificationRepository extends JpaRepository<OverdueNotification, Long> {
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM OverdueNotification n " +
           "WHERE n.memberId = :memberId AND n.isbn = :isbn AND n.notifiedDate > :since")
    boolean existsRecent(@Param("memberId") Integer memberId, @Param("isbn") String isbn,
                         @Param("since") LocalDateTime since);
}
