package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.ReservationDraftRepository;
import com.blps_lab1.demo.data.tables.ReservationDraft;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.ISystemReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemReservationService implements ISystemReservationService {

    private final ReservationDraftRepository reservationDraftRepository;

    public SystemReservationService(ReservationDraftRepository reservationDraftRepository) {
        this.reservationDraftRepository = reservationDraftRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void markAsEmailSentBySystem(Long id) {
        ReservationDraft draft = reservationDraftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Draft not found with id = " + id));

        if (draft.getCoverLetter() != null && draft.getCoverLetter().startsWith("[APPROVED_BY_ADMIN]")) {
            String originalLetter = draft.getCoverLetter().replace("[APPROVED_BY_ADMIN] ", "");
            draft.setCoverLetter("[APPROVED_BY_OWNER] " + originalLetter);
            reservationDraftRepository.save(draft);
        }
    }
}