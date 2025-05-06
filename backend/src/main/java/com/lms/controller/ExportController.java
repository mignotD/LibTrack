package com.lms.controller;

import com.lms.entity.Book;
import com.lms.entity.Transaction;
import com.lms.repository.BookRepository;
import com.lms.repository.TransactionRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;

    public ExportController(BookRepository bookRepository, TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/books")
    public ResponseEntity<InputStreamResource> exportBooks() {
        List<Book> books = bookRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("ISBN,Title,Author,Genre,Publisher,Stock,Available\n");

        for (Book b : books) {
            csv.append(escapeCsv(b.getIsbn())).append(",");
            csv.append(escapeCsv(b.getTitle())).append(",");
            csv.append(escapeCsv(b.getAuthor())).append(",");
            csv.append(escapeCsv(b.getGenre())).append(",");
            csv.append(escapeCsv(b.getPublisher())).append(",");
            csv.append(b.getStock()).append(",");
            csv.append(b.getAvailable()).append("\n");
        }

        return buildCsvResponse(csv.toString());
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("TransactionID,MemberID,MemberName,ISBN,BookTitle,BorrowDate,DueDate,ReturnDate,Fine,FinePaid\n");

        for (Transaction t : transactions) {
            csv.append(t.getTransactionId()).append(",");
            csv.append(t.getMember().getMemberId()).append(",");
            csv.append(escapeCsv(t.getMember().getName())).append(",");
            csv.append(escapeCsv(t.getBook().getIsbn())).append(",");
            csv.append(escapeCsv(t.getBook().getTitle())).append(",");
            csv.append(t.getBorrowDate()).append(",");
            csv.append(t.getDueDate()).append(",");
            csv.append(t.getReturnDate() != null ? t.getReturnDate() : "").append(",");
            csv.append(t.getFine()).append(",");
            csv.append(t.getFinePaid()).append("\n");
        }

        return buildCsvResponse(csv.toString());
    }

    @GetMapping("/transactions/my")
    public ResponseEntity<InputStreamResource> exportMyTransactions(@RequestAttribute("memberId") Integer memberId) {
        List<Transaction> transactions =
                transactionRepository.findByMemberMemberIdOrderByBorrowDateDesc(memberId);

        StringBuilder csv = new StringBuilder();
        csv.append("TransactionID,MemberID,MemberName,ISBN,BookTitle,BorrowDate,DueDate,ReturnDate,Fine,FinePaid\n");

        for (Transaction t : transactions) {
            csv.append(t.getTransactionId()).append(",");
            csv.append(t.getMember().getMemberId()).append(",");
            csv.append(escapeCsv(t.getMember().getName())).append(",");
            csv.append(escapeCsv(t.getBook().getIsbn())).append(",");
            csv.append(escapeCsv(t.getBook().getTitle())).append(",");
            csv.append(t.getBorrowDate()).append(",");
            csv.append(t.getDueDate()).append(",");
            csv.append(t.getReturnDate() != null ? t.getReturnDate() : "").append(",");
            csv.append(t.getFine()).append(",");
            csv.append(t.getFinePaid()).append("\n");
        }

        return buildCsvResponse(csv.toString());
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private ResponseEntity<InputStreamResource> buildCsvResponse(String csv) {
        ByteArrayInputStream bis = new ByteArrayInputStream(csv.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"libtrack_export.csv\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(bis));
    }
}
