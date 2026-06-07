package com.blps_lab1.demo.data.repository;

import com.blps_lab1.demo.data.tables.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPlaceId(Long placeId);
    List<Reservation> findByPlaceIdAndArrivalLessThanAndDepartureGreaterThan(
            Long placeId,
            LocalDate departure,
            LocalDate arrival
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.place.id = :placeId " +
            "AND r.arrival < :departure AND r.departure > :arrival")
    List<Reservation> findConflictsForUpdate(
            @Param("placeId") Long placeId,
            @Param("arrival") LocalDate arrival,
            @Param("departure") LocalDate departure
    );
}
