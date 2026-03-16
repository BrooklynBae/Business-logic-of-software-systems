package services;

import data.repository.ReservationRepository;
import data.tables.Reservation;
import dto.ReservationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private  final ReservationRepository reservationRepository;

    private ReservationDto toResponse(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getArrival(),
                reservation.getDeparture(),
                reservation.getGuestsAmount(),
                reservation.getPetsAmount(),
                reservation.getUser(),
                reservation.getPrice(),
                reservation.getPaymentType(),
                reservation.getPaymentMethod(),

        );
    }

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationDto> findAllByPlace(Long id_place) {
        return reservationRepository.findByPlace(id_place).stream()
                .map(reservation -> )
    }

    // прихдит креейт, отдаю дто, фулл заполненную.
    public ReservationDto createReservation()
}
