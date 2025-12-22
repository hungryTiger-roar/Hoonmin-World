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
