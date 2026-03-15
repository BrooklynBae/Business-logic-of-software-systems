package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.PlaceService;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;
}
