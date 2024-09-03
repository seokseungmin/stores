package com.springboot.stores.repository;

import com.springboot.stores.entity.Review;
import com.springboot.stores.entity.Store;
import com.springboot.stores.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteByStoreId(Long id);

    List<Review> findByStore(Store store);

    boolean existsByUserAndStore(User user, Store store);
}