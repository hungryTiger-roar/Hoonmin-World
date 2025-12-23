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

INSERT INTO account (user_id, pw, name, phone, birth, att_id, ticket) VALUES
('aa', '11', '김민수', '010-1111-1111', '1995-01-12', NULL, True),
('bb', '11', '이서연', '010-2222-2222', '1998-03-25', NULL, TRUE),
('cc', '11', '박준호', '010-3333-3333', '1992-07-08', NULL, FALSE),
('dd', '11', '최은지', '010-4444-4444', '2000-11-19', NULL, TRUE),
('ee', '11', '정하늘', '010-5555-5555', '1997-05-02', NULL, FALSE),
('ff', '11', '한지민', '010-6666-6666', '1994-09-14', NULL, FALSE),
('gg', '11', '오세훈', '010-7777-7777', '1990-12-30', NULL, TRUE),
('hh', '11', '유나',   '010-8888-8888', '2001-04-10', NULL, FALSE),
('ii', '11', '강동원', '010-9999-9999', '1989-06-21', NULL, TRUE),
('jj', '11', '문채원', '010-1010-1010', '1996-08-17', NULL, FALSE),
('staff', '11', '관리자', '010-0000-0000', '1999-12-12', null, true),

('1',  '11', '김민수', '010-1000-0001', '1990-01-01', NULL, FALSE),
('2',  '11', '이서연', '010-1000-0002', '1991-02-02', NULL, TRUE),
('3',  '11', '박준호', '010-1000-0003', '1992-03-03', NULL, FALSE),
('4',  '11', '최은지', '010-1000-0004', '1993-04-04', NULL, TRUE),
('5',  '11', '정하늘', '010-1000-0005', '1994-05-05', NULL, FALSE),
('6',  '11', '한지민', '010-1000-0006', '1995-06-06', NULL, FALSE),
('7',  '11', '오세훈', '010-1000-0007', '1996-07-07', NULL, TRUE),
('8',  '11', '유나',   '010-1000-0008', '1997-08-08', NULL, FALSE),
('9',  '11', '강동원', '010-1000-0009', '1998-09-09', NULL, TRUE),
('10', '11', '문채원', '010-1000-0010', '1999-10-10', NULL, FALSE),

('11', '11', '김지훈', '010-1000-0011', '2012-11-11', NULL, FALSE),
('12', '11', '박소연', '010-1000-0012', '2012-12-12', NULL, TRUE),
('13', '11', '이준혁', '010-1000-0013', '2012-01-13', NULL, FALSE),
('14', '11', '최수빈', '010-1000-0014', '2012-02-14', NULL, TRUE),
('15', '11', '정우성', '010-1000-0015', '2012-03-15', NULL, FALSE),
('16', '11', '윤아',   '010-1000-0016', '2012-04-16', NULL, FALSE),
('17', '11', '서강준', '010-1000-0017', '2012-05-17', NULL, TRUE),
('18', '11', '김태리', '010-1000-0018', '2012-06-18', NULL, FALSE),
('19', '11', '남주혁', '010-1000-0019', '2012-07-19', NULL, TRUE),
('20', '11', '수지',   '010-1000-0020', '2012-08-20', NULL, FALSE),

('21', '11', '송중기', '010-1000-0021', '2018-09-21', NULL, TRUE),
('22', '11', '아이유', '010-1000-0022', '2018-10-22', NULL, FALSE),
('23', '11', '박보검', '010-1000-0023', '2018-11-23', NULL, TRUE),
('24', '11', '김고은', '010-1000-0024', '2018-12-24', NULL, FALSE),
('25', '11', '이민호', '010-1000-0025', '2018-01-25', NULL, TRUE),
('26', '11', '한효주', '010-1000-0026', '2018-02-26', NULL, FALSE),
('27', '11', '조정석', '010-1000-0027', '2018-03-27', NULL, TRUE),
('28', '11', '김유정', '010-1000-0028', '2018-04-28', NULL, FALSE),
('29', '11', '지창욱', '010-1000-0029', '2018-05-29', NULL, TRUE),
('30', '11', '신세경', '010-1000-0030', '2018-06-30', NULL, FALSE),

('31', '11', '도경수', '010-1000-0031', '1990-07-01', NULL, FALSE),
('32', '11', '김혜수', '010-1000-0032', '1991-08-02', NULL, TRUE),
('33', '11', '이제훈', '010-1000-0033', '1992-09-03', NULL, FALSE),
('34', '11', '서예지', '010-1000-0034', '1993-10-04', NULL, TRUE),
('35', '11', '공유',   '010-1000-0035', '1994-11-05', NULL, FALSE),
('36', '11', '전지현', '010-1000-0036', '1995-12-06', NULL, TRUE),
('37', '11', '차은우', '010-1000-0037', '1996-01-07', NULL, FALSE),
('38', '11', '김지원', '010-1000-0038', '1997-02-08', NULL, TRUE),
('39', '11', '이동욱', '010-1000-0039', '1998-03-09', NULL, FALSE),
('40', '11', '손예진', '010-1000-0040', '1999-04-10', NULL, TRUE),

('41', '11', '황정민', '010-1000-0041', '1990-05-11', NULL, FALSE),
('42', '11', '김다미', '010-1000-0042', '1991-06-12', NULL, TRUE),
('43', '11', '이승기', '010-1000-0043', '1992-07-13', NULL, FALSE),
('44', '11', '박민영', '010-1000-0044', '1993-08-14', NULL, TRUE),
('45', '11', '유연석', '010-1000-0045', '1994-09-15', NULL, FALSE),
('46', '11', '한소희', '010-1000-0046', '1995-10-16', NULL, TRUE),
('47', '11', '정해인', '010-1000-0047', '1996-11-17', NULL, FALSE),
('48', '11', '김선호', '010-1000-0048', '1997-12-18', NULL, TRUE),
('49', '11', '이성경', '010-1000-0049', '1998-01-19', NULL, FALSE),
('50', '11', '박서준', '010-1000-0050', '1999-02-20', NULL, TRUE);

INSERT INTO item 
(item_name, item_price, item_count, item_pic, item_comment, item_category)
VALUES
('후니 머리띠',8000,100,'http://192.168.32.102:8080/uploaded/hoonihead.jpg','🎀 훈민월드의 인기 캐릭터 후니를 모티브로 제작된 머리띠입니다. 부드러운 착용감과 귀여운 디자인이 특징이며, 퍼레이드 관람이나 기념 촬영 시 착용하면 더욱 즐거운 추억을 남길 수 있어요 ✨','굿즈'),
( '미니 머리띠', 8000, 100, 'http://192.168.32.102:8080/uploaded/minihead.jpg', '💗 사랑스러운 미니 캐릭터 디자인의 머리띠로, 핑크 컬러와 리본 포인트가 돋보입니다. 어린이부터 성인까지 부담 없이 착용 가능해 놀이공원 방문 기념 아이템으로 딱 좋아요 🎠', '굿즈'),
( '후니 티셔츠', 29000, 100, 'http://192.168.32.102:8080/uploaded/hoonit.jpg', '👕 훈민월드 대표 캐릭터 후니가 프린팅된 티셔츠입니다. 면 소재를 사용해 착용감이 뛰어나며, 일상복으로도 활용 가능해 놀이공원 방문 기념 패션 아이템으로 추천드려요 😊', '의류'),
( '미니 티셔츠', 29000, 100, 'http://192.168.32.102:8080/uploaded/minit.jpg', '🌸 밝고 귀여운 미니 캐릭터가 돋보이는 티셔츠입니다. 부드러운 촉감과 깔끔한 디자인으로 커플룩이나 가족 단체복으로도 잘 어울리는 인기 상품이에요 👨‍👩‍👧‍👦', '의류'),
( '후니미니 키링', 5000, 100, 'http://192.168.32.102:8080/uploaded/keyring.jpg', '🔑 후니와 미니 캐릭터가 함께 디자인된 키링입니다. 가방이나 열쇠에 간편하게 부착할 수 있어 실용적이며, 작은 소품이지만 훈민월드의 추억을 오래 간직할 수 있어요 💕', '액세서리'),
( '후니미니 가방', 5000, 100, 'http://192.168.32.102:8080/uploaded/bag.jpg', '👜 훈민월드 캐릭터 후니미니가 프린팅된 가방입니다. 가볍고 실용적인 수납공간을 제공해 놀이공원 내 이동이나 기념품 보관용으로 사용하기 좋아요 🎁', '가방'),
( '후니미니 퍼레이드 인형', 25000, 100, 'http://192.168.32.102:8080/uploaded/parade.jpg', '🎉 퍼레이드 의상을 입은 후니미니 인형으로, 훈민월드 퍼레이드의 즐거운 분위기를 그대로 담아냈습니다. 아이 선물이나 캐릭터 컬렉션용으로 특히 인기가 많아요 🧸', '인형'),
( '후니미니 인형', 22000, 100, 'http://192.168.32.102:8080/uploaded/doll.jpg', '🧸 후니와 미니의 기본 모습을 담은 인형입니다. 부드러운 촉감과 안정적인 마감으로 남녀노소 모두에게 사랑받는 훈민월드 대표 캐릭터 상품이에요 💖', '인형'),
( '후니미니 휴대폰 케이스', 18000, 100, 'http://192.168.32.102:8080/uploaded/phone.jpg', '📱 놀이공원 배경과 후니미니 캐릭터가 함께 디자인된 휴대폰 케이스입니다. 일상 속에서도 훈민월드의 감성을 느낄 수 있으며, 기종별 호환을 고려해 제작되었습니다 ✨', '전자기기'),
( '후니미니 그립톡', 8000, 100, 'http://192.168.32.102:8080/uploaded/griptok.jpg', '🤳 후니미니 캐릭터가 디자인된 그립톡으로, 스마트폰 사용 시 안정적인 그립감을 제공합니다. 귀여운 디자인과 실용성을 동시에 갖춘 인기 굿즈예요 ⭐', '액세서리'),
( '후니미니 지비츠', 13000, 100, 'http://192.168.32.102:8080/uploaded/jibbitz.jpg', '👟 후니미니와 훈민월드를 테마로 한 다양한 디자인의 지비츠 세트입니다. 신발이나 가방을 개성 있게 꾸밀 수 있어 어린이와 청소년에게 특히 인기가 많아요 🎨', '굿즈');

-- 경훈 홈 : 192.168.55.21 경훈 싸피 : 192.168.32.102
INSERT INTO attraction
(att_name, att_pic, att_capacity, att_comment, att_able, att_category, att_total)
VALUES
('빙글빙글 회전목마', 'http://192.168.32.102:8080/uploaded/binglebingle.jpg', 20, '아이부터 어른까지 누구나 즐길 수 있는 아기자기한 회전목마입니다.', true, '어린이', 0),
('4D 슈팅 어드벤처', 'http://192.168.32.102:8080/uploaded/4dshot.jpg', 16, '움직이는 좌석과 실감나는 효과! 직접 쏘며 즐기는 4D 체험형 어트랙션.', true, '가족', 0),
('범퍼카 레이스', 'http://192.168.32.102:8080/uploaded/bumbercar.jpg', 24, '친구들과 부딪히며 스트레스를 날릴 수 있는 인기 만점 범퍼카!', true, '어린이', 0),
('스카이 레이싱', 'http://192.168.32.102:8080/uploaded/skyracing.jpg', 12, '하늘을 나는 듯한 스피드! 짜릿한 공중 레이싱 어트랙션.', true, '스릴', 0),
('바이킹', 'http://192.168.32.102:8080/uploaded/viking.jpg', 32, '앞뒤로 크게 흔들리는 전통의 공포! 스릴을 즐기는 분께 추천.', true, '스릴', 0),
('롤러코스터', 'http://192.168.32.102:8080/uploaded/rollercost.png', 20, '급강하와 급회전을 동시에! 놀이공원의 꽃, 롤러코스터.', true, '스릴', 0),
('메가 스윙', 'http://192.168.32.102:8080/uploaded/megaswing.jpg', 16, '거대한 그네가 하늘 끝까지! 아찔한 높이를 경험하세요.', true, '스릴', 0),
('워터 슬라이드', 'http://192.168.32.102:8080/uploaded/waterslide.jpg', 30, '시원한 물과 함께 즐기는 여름 한정 인기 어트랙션!', true, '가족', 0),
('대관람차', 'http://192.168.32.102:8080/uploaded/ferriswheel.jpg', 40, '놀이공원을 한눈에! 연인과 함께 타기 좋은 로맨틱 어트랙션.', true, '가족', 0);

Insert into home_image (home_image) values
('http://192.168.32.102:8080/uploaded/banner1.jpg'),
('http://192.168.32.102:8080/uploaded/banner2.jpg'),
('http://192.168.32.102:8080/uploaded/banner3.jpg');

Insert into buy_image (buy_image) values
('http://192.168.32.102:8080/uploaded/itembanner1.jpg'),
('http://192.168.32.102:8080/uploaded/itembanner2.jpg');
-- orders/order_detail dummy data (snapshot)

INSERT INTO orders (order_id, user_id, order_store, order_received, order_time, order_received_time) VALUES (1, 'aa', 1, 0, '2025-12-21 15:07:10', NULL);
INSERT INTO orders (order_id, user_id, order_store, order_received, order_time, order_received_time) VALUES (2, 'bb', 3, 0, '2025-12-21 15:07:45', NULL);
INSERT INTO orders (order_id, user_id, order_store, order_received, order_time, order_received_time) VALUES (3, 'dd', 1, 0, '2025-12-21 15:07:58', NULL);
INSERT INTO orders (order_id, user_id, order_store, order_received, order_time, order_received_time) VALUES (4, 'ee', 2, 1, '2025-12-21 15:08:11', '2025-12-21 15:09:19');
INSERT INTO orders (order_id, user_id, order_store, order_received, order_time, order_received_time) VALUES (5, 'ff', 1, 0, '2025-12-21 15:41:11', NULL);

INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (1, 1, 1, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (2, 1, 2, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (3, 1, 4, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (4, 2, 6, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (5, 2, 2, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (6, 2, 4, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (7, 3, 6, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (8, 3, 7, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (9, 3, 5, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (10, 4, 1, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (11, 4, 7, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (12, 4, 5, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (13, 5, 3, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (14, 5, 7, 2, 0);
INSERT INTO order_detail (detail_id, order_id, item_id, order_quantity, detail_review) VALUES (15, 5, 5, 2, 0);

INSERT INTO home_board (board_title, board_content, board_date)
VALUES
('📢 놀이공원 오픈 안내', '훈민월드가 드디어 오픈했습니다! 다양한 어트랙션과 즐길 거리가 여러분을 기다립니다.', '2024-01-05 10:00:00'),
('🎡 대관람차 점검 완료 안내', '대관람차 정기 점검이 완료되어 정상 운영을 시작합니다.', '2024-03-12 09:30:00'),
('⚠️ 바이킹 임시 운행 중단 안내', '안전 점검으로 인해 바이킹 어트랙션 운행이 일시 중단됩니다.', '2024-06-18 14:00:00'),
('🎉 여름 시즌 이벤트 시작!', '여름 시즌을 맞아 다양한 할인 이벤트와 공연이 진행됩니다.', '2024-07-01 11:00:00'),
('🚧 워터 슬라이드 정비 공지', '시설 정비로 인해 워터 슬라이드 운영이 잠시 중단됩니다.', '2024-09-10 16:30:00'),
('🎃 할로윈 특별 행사 안내', '할로윈 기간 동안 특별 퍼레이드와 코스튬 이벤트가 열립니다.', '2024-10-25 13:00:00'),
('❄️ 겨울 시즌 운영 시간 변경 안내', '겨울 시즌에는 놀이공원 운영 시간이 단축됩니다.', '2024-12-05 10:00:00'),
('🎆 새해맞이 불꽃놀이 안내', '2025년 새해를 맞아 대규모 불꽃놀이 행사가 진행됩니다.', '2024-12-31 23:00:00'),
('👨‍👩‍👧‍👦 패밀리 데이 이벤트 안내', '가족 방문객을 위한 패밀리 데이 특별 혜택을 제공합니다.', '2025-03-15 12:00:00'),
('🔔 놀이공원 정상 운영 안내', '현재 모든 어트랙션이 정상 운영 중입니다. 즐거운 관람 되세요!', '2025-12-22 09:00:00');



