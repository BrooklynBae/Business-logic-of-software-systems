package services;

import data.repository.ReservationRepository;
import dto.ReservationDto;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private  final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // прихдит креейт, отдаю дто, фулл заполненную.
    public ReservationDto createReservation()
}
