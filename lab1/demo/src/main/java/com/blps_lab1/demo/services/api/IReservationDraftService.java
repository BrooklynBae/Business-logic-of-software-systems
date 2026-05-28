package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.Place;
import com.blps_lab1.demo.data.tables.ReservationDraft;
import com.blps_lab1.demo.dto.DateRequest;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.dto.ReservationRequest;

public interface IReservationDraftService {
    public ReservationDto createDraft(ReservationRequest request);

    public ReservationDto updateDate(Long id, DateRequest dateRequest);

    public void deleteExpiredDrafts();
    //ReservationDraft findEntityById(Long id);

}
