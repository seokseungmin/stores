DROP DATABASE  `reservation_db` ;
CREATE DATABASE  reservation_db;
use reservation_db;

CREATE TABLE Users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(50) UNIQUE NOT NULL,
                       phone VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT UC_Username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Stores (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) UNIQUE NOT NULL,
                        location VARCHAR(255) NOT NULL,
                        description TEXT,
                        rating DOUBLE,                -- 별점 필드 추가
                        distance DOUBLE,              -- 거리 필드 추가
                        owner_id BIGINT,
                        FOREIGN KEY (owner_id) REFERENCES Users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Reservations (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              store_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              reservation_time TIMESTAMP NOT NULL,
                              status VARCHAR(30) NOT NULL,
                              FOREIGN KEY (store_id) REFERENCES Stores(id),
                              FOREIGN KEY (user_id) REFERENCES Users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE Reviews (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         store_id BIGINT,
                         user_id BIGINT,
                         rating DOUBLE NOT NULL,
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (store_id) REFERENCES Stores(id),
                         FOREIGN KEY (user_id) REFERENCES Users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

