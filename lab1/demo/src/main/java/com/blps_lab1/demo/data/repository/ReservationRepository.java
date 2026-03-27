package data.repository;

import data.tables.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
