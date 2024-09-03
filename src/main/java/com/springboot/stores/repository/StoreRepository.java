package com.springboot.stores.repository;

import com.springboot.stores.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    long countByName(String name);

    List<Store> findByNameContaining(String search);

    Optional<Store> findByName(String name);

    // ownerId로 가게 이름을 찾는 메소드
    @Query("SELECT s.name FROM Store s WHERE s.owner.id = :ownerId")
    List<String> findStoreNamesByOwnerId(@Param("ownerId") Long ownerId);

}