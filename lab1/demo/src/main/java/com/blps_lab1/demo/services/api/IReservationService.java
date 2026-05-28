package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.Place;
import com.blps_lab1.demo.data.tables.Reservation;
import com.blps_lab1.demo.data.tables.ReservationDraft;
import com.blps_lab1.demo.dto.CreateReservationEntityRequest;
import com.blps_lab1.demo.dto.DateDto;
import com.blps_lab1.demo.dto.DateRequest;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.dto.ReservationRequest;

import java.time.LocalDate;
import java.util.List;

public interface IReservationService {
    //ReservationDto createDraft(ReservationRequest request);
    ReservationDto findReservation(long id);
    Long confirmReservation(Long id);
    void deleteReservation(Long id);
    ReservationDto createReservationEntity(CreateReservationEntityRequest request);
    List<DateDto> findAllReservedDates(Long placeId);
    void ensureDatesAvailable(Long placeId, LocalDate arrival, LocalDate departure);
}
