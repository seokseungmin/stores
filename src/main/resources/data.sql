-- Users 테이블 초기 데이터 삽입
-- Users 테이블 초기 데이터 삽입
INSERT INTO Users (username, password, role) VALUES
                                                 ('partner1', '$2a$10$7QcvTF6j.mwRPPyH0ZCw7Od.89P1OuROmvIvke1kPb6Zm6r6aA6Bi', 'ROLE_PARTNER'),
                                                 ('partner2', '$2a$10$9bDd.pR0SYl0q0rU6sQsNeP5BxwTm9.CUalR3Ge04tSIn1tn7/KkO', 'ROLE_PARTNER'),
                                                 ('user1', '$2a$10$U7Qy3Fcxn.8Mgzz/6oOCqO0KMm29gLFtmJzDpQ8XpsNkQkWt8JWEq', 'ROLE_USER'),
                                                 ('user2', '$2a$10$SuK7FFH9Q/Zntn4wKbgL6OXwnG2SgY1rFzQOCtGHawVeIf8P6TmZi', 'ROLE_USER');

--partner1: password123
--partner2: mypassword
--user1: userpassword
--user2: securepass

-- Stores 테이블 초기 데이터 삽입
INSERT INTO Stores (name, location, description, owner_id) VALUES
                                                               ('Restaurant A', 'Seoul, Korea', 'A popular restaurant in Seoul', 1),
                                                               ('Cafe B', 'Busan, Korea', 'A cozy cafe in Busan', 2);

-- Reservations 테이블 초기 데이터 삽입
INSERT INTO Reservations (store_id, user_id, reservation_time, status) VALUES
                                                                           (1, 3, '2024-09-01 18:30:00', 'PENDING'),
                                                                           (2, 4, '2024-09-02 14:00:00', 'CONFIRMED');

-- Reviews 테이블 초기 데이터 삽입
INSERT INTO Reviews (store_id, user_id, rating, comment) VALUES
                                                             (1, 3, 5, 'Great food and service!'),
                                                             (2, 4, 4, 'Nice place but a bit crowded.');

