drop database HMworld;
select @@global.transaction_isolation, @@transaction_isolation;
set @@transaction_isolation="read-committed";

create database HMworld;
use HMworld;
CREATE TABLE home_board(
	board_id INT auto_increment primary key,
    board_title VARCHAR(200) NOT NULL,
    board_content TEXT NOT NULL,
    board_date DATETIME default current_timestamp
);

CREATE TABLE home_image(
	home_id INT AUTO_INCREMENT PRIMARY KEY,
    home_image VARCHAR(255) NOT NULL
);

CREATE TABLE buy_image(
	buy_id INT AUTO_INCREMENT PRIMARY KEY,
    buy_image VARCHAR(255) NOT NULL
);

CREATE TABLE attraction (
    att_id INT AUTO_INCREMENT PRIMARY KEY,
    att_name VARCHAR(100),
    att_pic VARCHAR(255),    
    att_capacity INT,
    att_comment TEXT,
    att_able boolean default true,
    att_category VARCHAR(100),
    att_total INT default 0
);

CREATE TABLE item (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    item_price INT default 0,
    item_count INT DEFAULT 0,
    item_pic VARCHAR(255),
    item_comment TEXT,
    item_category VARCHAR(100),
    item_time DATETIME NOT NULL DEFAULT current_timestamp
);

CREATE TABLE account (
    user_id VARCHAR(50) PRIMARY KEY,
    pw VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    birth VARCHAR(20),
    att_id INT NULL,
    ticket BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT fk_account_booking
        FOREIGN KEY (att_id) REFERENCES attraction(att_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    order_store INT NOT NULL,
    order_received boolean NOT NULL default false,
    order_time DATETIME NOT NULL DEFAULT current_timestamp,
    order_received_time DATETIME NULL,
    
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES account(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE order_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    item_id INT,
    order_quantity INT NOT NULL,
    detail_review boolean not null default false,

    CONSTRAINT fk_detail_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_detail_item
        FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE item_review (
    item_review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    item_id INT,
    item_review_comment TEXT,
    item_rating float not null default 5,
    item_time DATETIME NOT NULL DEFAULT current_timestamp,

    CONSTRAINT fk_item_review_user
        FOREIGN KEY (user_id) REFERENCES account(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_item_review_item
        FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE attraction_review (
    att_review_id INT AUTO_INCREMENT PRIMARY KEY,
    att_id INT,
    user_id VARCHAR(50),
    att_review_comment TEXT,
    att_rating float not null default 5,
    att_time DATETIME NOT NULL DEFAULT current_timestamp,

    CONSTRAINT fk_att_review_attraction
        FOREIGN KEY (att_id) REFERENCES attraction(att_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_att_review_user
        FOREIGN KEY (user_id) REFERENCES account(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE attraction_line (
    line_id INT AUTO_INCREMENT PRIMARY KEY,
    att_id INT NOT NULL,

    CONSTRAINT fk_line_attraction
        FOREIGN KEY (att_id) REFERENCES attraction(att_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

create table attraction_line_member(
	id int auto_increment primary key,
    line_id int NOT NULL,
    user_id varchar(50) NOT NULL,
    
    constraint fk_line_member
		foreign key(line_id) references attraction_line(line_id)
        on delete cascade
        on update cascade,
        
	constraint fk_line_member_user
		foreign key(user_id) references account(user_id)
        on delete cascade
        on update cascade
);

CREATE TABLE friend (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    friend_id VARCHAR(50),
    friend_party boolean default false,

    CONSTRAINT fk_friend_userattraction_line
        FOREIGN KEY (user_id) REFERENCES account(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_friend_target
        FOREIGN KEY (friend_id) REFERENCES account(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO account (user_id, pw, name, phone, birth, att_id, ticket) VALUES
('aa', '11', '김민수', '010-1111-1111', '1995-01-12', NULL, FALSE),
('bb', '11', '이서연', '010-2222-2222', '1998-03-25', NULL, TRUE),
('cc', '11', '박준호', '010-3333-3333', '1992-07-08', NULL, FALSE),
('dd', '11', '최은지', '010-4444-4444', '2000-11-19', NULL, TRUE),
('ee', '11', '정하늘', '010-5555-5555', '1997-05-02', NULL, FALSE),
('ff', '11', '한지민', '010-6666-6666', '1994-09-14', NULL, FALSE),
('gg', '11', '오세훈', '010-7777-7777', '1990-12-30', NULL, TRUE),
('hh', '11', '유나',   '010-8888-8888', '2001-04-10', NULL, FALSE),
('ii', '11', '강동원', '010-9999-9999', '1989-06-21', NULL, TRUE),
('jj', '11', '문채원', '010-1010-1010', '1996-08-17', NULL, FALSE);

INSERT INTO item 
(item_name, item_price, item_count, item_pic, item_comment, item_category)
VALUES
('놀이공원 티켓', 50000, 100, 'ticket.png', '하루 종일 자유이용 가능한 티켓', '티켓'),
('패스트패스', 30000, 50, 'fastpass.png', '대기 없이 바로 이용 가능한 패스', '티켓'),
('토끼 인형', 15000, 30, 'rabbit_doll.png', '귀여운 토끼 캐릭터 인형', '굿즈'),
('롤러코스터 미니어처', 20000, 20, 'coaster_model.png', '인기 롤러코스터 미니 모형', '굿즈'),
('놀이공원 머그컵', 12000, 40, 'mug.png', '훈민월드 로고 머그컵', '굿즈'),
('치즈 핫도그', 6000, 200, 'hotdog.png', '바삭한 치즈 핫도그', '푸드'),
('츄러스', 5000, 150, 'churros.png', '달콤한 시나몬 츄러스', '푸드'),
('콜라', 3000, 300, 'cola.png', '시원한 탄산음료', '음료'),
('아이스크림', 4000, 120, 'icecream.png', '여름에 인기 많은 아이스크림', '푸드'),
('캐릭터 풍선', 8000, 60, 'balloon.png', '아이들에게 인기 많은 캐릭터 풍선', '굿즈');


CREATE TABLE fcm_token (
    token VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fcm_token_user
        FOREIGN KEY (user_id) REFERENCES account(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE push_notification (
    push_id INT AUTO_INCREMENT PRIMARY KEY,
    push_title VARCHAR(200) NOT NULL,
    push_body TEXT NOT NULL,
    push_type VARCHAR(20) NOT NULL,
    scheduled_at DATETIME NULL,
    repeat_days VARCHAR(30) NULL,
    repeat_time VARCHAR(5) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    last_sent_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
