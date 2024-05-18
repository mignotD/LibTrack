CREATE TABLE IF NOT EXISTS overdue_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    notified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_overdue_member FOREIGN KEY (member_id) REFERENCES members(member_id),
    CONSTRAINT fk_overdue_book FOREIGN KEY (isbn) REFERENCES books(isbn)
);
