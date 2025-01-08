package com.lms.service;

import com.lms.entity.OverdueNotification;
import com.lms.entity.Reservation;
import com.lms.entity.Transaction;
import com.lms.repository.OverdueNotificationRepository;
import com.lms.repository.ReservationRepository;
import com.lms.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(NotificationSchedulerService.class);

    private final TransactionRepository transactionRepository;
    private final ReservationRepository reservationRepository;
    private final OverdueNotificationRepository overdueNotificationRepository;
    private final EmailService emailService;

    public NotificationSchedulerService(TransactionRepository transactionRepository,
                                        ReservationRepository reservationRepository,
                                        OverdueNotificationRepository overdueNotificationRepository,
                                        EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.reservationRepository = reservationRepository;
        this.overdueNotificationRepository = overdueNotificationRepository;
        this.emailService = emailService;
    }

    @Scheduled(initialDelay = 3600000, fixedRate = 86400000)
    public void checkOverdueBooks() {
        log.info("Checking overdue books...");
        List<Transaction> overdue = transactionRepository.findOverdueWithEmail(LocalDate.now());
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int sent = 0;

        for (Transaction t : overdue) {
            try {
                Integer memberId = t.getMember().getMemberId();
                String isbn = t.getBook().getIsbn();

                if (overdueNotificationRepository.existsRecent(memberId, isbn, cutoff)) {
                    continue;
                }

                String email = t.getMember().getEmail();
                String name = t.getMember().getName();
                String bookTitle = t.getBook().getTitle();
                String dueDate = t.getDueDate().toString();

                emailService.sendOverdueNotice(email, name, bookTitle, dueDate);
                overdueNotificationRepository.save(new OverdueNotification(memberId, isbn));
                sent++;
                log.info("Overdue notice sent to {} for book '{}'", email, bookTitle);
            } catch (Exception e) {
                log.error("Failed to send overdue notice for transaction {}: {}", t.getTransactionId(), e.getMessage());
            }
        }

        log.info("Overdue check completed. {} notifications sent ({} skipped).", sent, overdue.size() - sent);
    }

    @Scheduled(initialDelay = 3600000, fixedRate = 21600000)
    public void checkReservationAvailability() {
        log.info("Checking reservation availability...");
        List<Reservation> available =
                reservationRepository.findByFulfilledFalseAndBookAvailableGreaterThan(0);

        for (Reservation r : available) {
            try {
                String email = r.getMember().getEmail();
                String name = r.getMember().getName();
                String bookTitle = r.getBook().getTitle();

                emailService.sendReservationAvailableNotice(email, name, bookTitle);
                r.setFulfilled(true);
                reservationRepository.save(r);
                log.info("Reservation available notice sent to {} for book '{}'", email, bookTitle);
            } catch (Exception e) {
                log.error("Failed to process reservation {}: {}", r.getReservationId(), e.getMessage());
            }
        }

        log.info("Reservation availability check completed. {} notifications sent.", available.size());
    }
}
