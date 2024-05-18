create table if not exists members
(
    member_id  int auto_increment primary key,
    name       varchar(100) not null,
    email      varchar(100) not null unique,
    password   varchar(255) not null,
    phone      varchar(20),
    address    varchar(255),
    join_date  date,
    status     varchar(20)  not null default 'Active',
    role       varchar(20)  not null default 'member',
    created_at datetime              default current_timestamp
);

create table if not exists books
(
    isbn             varchar(20) primary key,
    title            varchar(255) not null,
    author           varchar(255) not null,
    publisher        varchar(255),
    genre            varchar(100),
    publication_year int,
    stock            int          not null default 0,
    available        int          not null default 0,
    cover_url        varchar(500)
);

create table if not exists transactions
(
    transaction_id int auto_increment primary key,
    isbn           varchar(20)  not null,
    member_id      int          not null,
    borrow_date    date         not null,
    due_date       date         not null,
    return_date    date,
    fine           decimal(10, 2) default 0.00,
    fine_paid      tinyint(1)     default 0,
    foreign key (isbn) references books (isbn),
    foreign key (member_id) references members (member_id)
);

create table if not exists reviews
(
    review_id   int auto_increment primary key,
    isbn        varchar(20) not null,
    member_id   int         not null,
    rating      int         not null,
    comment     text,
    review_date datetime default current_timestamp,
    foreign key (isbn) references books (isbn),
    foreign key (member_id) references members (member_id)
);

create table if not exists wishlist
(
    wishlist_id int auto_increment primary key,
    isbn        varchar(20) not null,
    member_id   int         not null,
    added_date  datetime default current_timestamp,
    foreign key (isbn) references books (isbn),
    foreign key (member_id) references members (member_id)
);

create table if not exists book_requests
(
    request_id   int auto_increment primary key,
    member_id    int          not null,
    title        varchar(255) not null,
    author       varchar(255),
    publisher    varchar(255),
    isbn         varchar(20),
    status       varchar(20)  not null default 'Pending',
    request_date datetime              default current_timestamp,
    foreign key (member_id) references members (member_id)
);

create table if not exists reservations
(
    reservation_id   int auto_increment primary key,
    isbn             varchar(20) not null,
    member_id        int         not null,
    reservation_date datetime default current_timestamp,
    fulfilled        tinyint(1)  not null default 0,
    foreign key (isbn) references books (isbn),
    foreign key (member_id) references members (member_id)
);

create table if not exists password_reset_tokens
(
    token_id    int auto_increment primary key,
    member_id   int          not null,
    token       varchar(255) not null unique,
    expiry_date datetime     not null,
    foreign key (member_id) references members (member_id)
);

create table if not exists audit_log
(
    log_id    int auto_increment primary key,
    member_id int,
    action    varchar(255) not null,
    details   text,
    timestamp datetime default current_timestamp,
    foreign key (member_id) references members (member_id)
);

create table if not exists borrowing_limits
(
    limit_id          int auto_increment primary key,
    member_type       varchar(50) not null unique,
    max_books         int         not null default 5,
    loan_duration_days int        not null default 14
);

-- Indexes
create index if not exists idx_transaction_member on transactions (member_id);
create index if not exists idx_transaction_isbn on transactions (isbn);
create index if not exists idx_transaction_return on transactions (return_date);
create index if not exists idx_transaction_due on transactions (due_date);
create index if not exists idx_book_title on books (title);
create index if not exists idx_book_author on books (author);
create index if not exists idx_book_genre on books (genre);
create index if not exists idx_review_book on reviews (isbn);
create index if not exists idx_wishlist_member on wishlist (member_id);
create index if not exists idx_auditlog_time on audit_log (timestamp);

-- Seed data
insert ignore into borrowing_limits (member_type, max_books, loan_duration_days)
values ('member', 3, 7);
insert ignore into borrowing_limits (member_type, max_books, loan_duration_days)
values ('admin', 10, 21);
