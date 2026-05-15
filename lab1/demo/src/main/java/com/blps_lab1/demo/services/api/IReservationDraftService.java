package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.dto.DateRequest;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.dto.ReservationRequest;

public interface IReservationDraftService {
    public ReservationDto createDraft(ReservationRequest request);

    public ReservationDto updateDate(Long id, DateRequest dateRequest);

}
