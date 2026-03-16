package services;

import data.repository.ReservationRepository;
import data.tables.Reservation;
import dto.DateDto;
import dto.PlaceDto;
import dto.ReservationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private  final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private ReservationDto toReservationDto(Reservation reservation) {
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
                reservation.getPlace().getOwner()
        );
    }

    public ReservationDto findReservation(long id) {
        return toReservationDto(placeRepository.getById(id));
    }

    // прихдит креейт, отдаю дто, фулл заполненную.
    public ReservationDto createReservation()
}
