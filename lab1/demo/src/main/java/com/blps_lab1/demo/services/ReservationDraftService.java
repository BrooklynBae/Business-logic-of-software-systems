package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.ReservationDraftRepository;
import com.blps_lab1.demo.data.tables.*;
import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.*;
import com.blps_lab1.demo.services.utils.JmsTaskProducer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class ReservationDraftService implements IReservationDraftService {
    private final ReservationDraftRepository reservationDraftRepository;
    private final IReservationService reservationService;
    private final IUserService userService;
    private final IPlaceService placeService;
    private final IServiceOptionService serviceOptionService;
    private final JmsTaskProducer jmsTaskProducer;
    public ReservationDraftService(ReservationDraftRepository reservationDraftRepository,
                                   IReservationService reservationService,
                                   IUserService userService,
                                   IPlaceService placeService,
                                   IServiceOptionService serviceOptionService,
                                    JmsTaskProducer jmsTaskProducer) {
        this.reservationDraftRepository = reservationDraftRepository;
        this.reservationService = reservationService;
        this.userService = userService;
        this.placeService = placeService;
        this.serviceOptionService = serviceOptionService;
        this.jmsTaskProducer = jmsTaskProducer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("permitAll()")
    public ReservationDto createDraft(ReservationRequest request) {
        if (request == null || request.getIdPlace() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Required parameters cannot be null");
        }
        if (request.getArrival() == null || request.getDeparture() == null) {
            throw new BadRequestException("Arrival and departure dates must be specified");
        }
        if (request.getGuestsAmount() == null || request.getGuestsAmount() <= 0) {
            throw new BadRequestException("Guests amount must be a positive integer");
        }

        Place place = placeService.findEntityById(request.getIdPlace());
        User user = userService.findEntityById(request.getUserId());
        Set<ServiceOption> selectedOptions = new HashSet<>(serviceOptionService.findEntitiesByIds(request.getServiceOptionIds()));

        reservationService.ensureDatesAvailable(request.getIdPlace(), request.getArrival(), request.getDeparture());

        ReservationDraft reservationDraft = new ReservationDraft();
        reservationDraft.setUser(user);
        reservationDraft.setPlace(place);
        reservationDraft.setArrival(request.getArrival());
        reservationDraft.setDeparture(request.getDeparture());
        reservationDraft.setGuestsAmount(request.getGuestsAmount());
        reservationDraft.setPetsAmount(request.getPetsAmount() != null ? request.getPetsAmount() : 0);
        reservationDraft.setPrice(1000.0);
        reservationDraft.setPlaceType(place.getPlaceType());
        reservationDraft.setServiceOptions(selectedOptions);

        if (place.getOwner() != null && Boolean.TRUE.equals(place.getOwner().getRequirenmentsMessage())) {
            if (request.getCoverLetter() == null || request.getCoverLetter().trim().isBlank()) {
                throw new BadRequestException("The owner of this place requires a cover letter for reservation.");
            }
            reservationDraft.setCoverLetter("[PENDING_ADMIN] " + request.getCoverLetter());
        } else {
            reservationDraft.setCoverLetter(request.getCoverLetter());
        }

        ReservationDraft saved = reservationDraftRepository.save(reservationDraft);

        if (place.getOwner() != null && Boolean.TRUE.equals(place.getOwner().getRequirenmentsMessage())) {
            jmsTaskProducer.sendToQueue("draft.moderation", new TaskMessage(saved.getId(), "MODERATE"));
        }

        return ReservationDto.builder()
                .id(saved.getId())
                .arrival(saved.getArrival())
                .departure(saved.getDeparture())
                .coverLetter(saved.getCoverLetter())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MODERATE_DRAFTS')")
    public void moderateByAdmin(Long id, boolean approved) {
        ReservationDraft draft = findEntityById(id);
        if (draft.getCoverLetter() == null || !draft.getCoverLetter().startsWith("[PENDING_ADMIN]")) {
            throw new BadRequestException("Draft is not pending admin moderation");
        }

        String originalLetter = draft.getCoverLetter().replace("[PENDING_ADMIN] ", "");

        if (approved) {
            draft.setCoverLetter("[APPROVED_BY_ADMIN] " + originalLetter);
            reservationDraftRepository.save(draft);
            jmsTaskProducer.sendToQueue("mail.sending", new TaskMessage(draft.getId(), "SEND_EMAIL"));
        } else {
            draft.setCoverLetter("[REJECTED] " + originalLetter);
            reservationDraftRepository.save(draft);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_CONFIRM_RESERVATIONS') and @appSecurity.isDraftPlaceOwner(#a0, authentication.name)")
    public void confirmByOwner(Long id, boolean approved) {
        ReservationDraft draft = findEntityById(id);

        if (draft.getCoverLetter() == null || !draft.getCoverLetter().startsWith("[EMAIL_SENT_TO_OWNER]")) {
            throw new BadRequestException("Draft is not in a state waiting for owner confirmation. Current state: " + draft.getCoverLetter());
        }

        String originalLetter = draft.getCoverLetter().replace("[EMAIL_SENT_TO_OWNER] ", "");

        if (approved) {
            jmsTaskProducer.sendToQueue("reservation.confirmation", new TaskMessage(draft.getId(), "CREATE_RESERVATION"));
        } else {
            draft.setCoverLetter("[REJECTED_BY_OWNER] " + originalLetter);
            reservationDraftRepository.save(draft);
        }
    }

    @Override
    public ReservationDraft findEntityById(Long id) {
        return reservationDraftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Draft not found with id = " + id));
    }

    @Override
    public void removeDraft(Long id) {
        reservationDraftRepository.deleteById(id);
    }

    @Override public ReservationDto updateDate(Long id, DateRequest r) { return null; }
    @Override public void deleteExpiredDrafts() {}
}