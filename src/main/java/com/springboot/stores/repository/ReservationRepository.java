package com.springboot.stores.repository;


import com.springboot.stores.entity.Reservation;
import com.springboot.stores.entity.ReservationStatus;
import com.springboot.stores.entity.Store;
import com.springboot.stores.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    void deleteByStoreId(Long id);

    Optional<Reservation> findByUserAndReservationTime(User user, LocalDateTime requestedReservationTime);

    boolean existsByStoreAndReservationTime(Store store, LocalDateTime reservationTime);

    boolean existsByUserAndStoreAndStatus(User user, Store store, ReservationStatus reservationStatus);

    @Query("SELECT r FROM Reservation r WHERE r.store.name = :storeName AND DATE(r.reservationTime) = DATE(:reservationDate)")
    List<Reservation> findReservationsByStoreNameAndDate(
            @Param("storeName") String storeName,
            @Param("reservationDate") LocalDateTime reservationDate);
}
