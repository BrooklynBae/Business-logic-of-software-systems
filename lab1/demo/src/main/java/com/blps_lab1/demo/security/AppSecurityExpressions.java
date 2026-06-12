package com.blps_lab1.demo.security;

import com.blps_lab1.demo.data.repository.ReservationDraftRepository;
import com.blps_lab1.demo.data.repository.UserRepository;
import com.blps_lab1.demo.data.repository.PlaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("appSecurity")
public class AppSecurityExpressions {

    private final ReservationDraftRepository draftRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public AppSecurityExpressions(ReservationDraftRepository draftRepository,
                                  UserRepository userRepository,
                                  PlaceRepository placeRepository) {
        this.draftRepository = draftRepository;
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
    }

    @Transactional(readOnly = true)
    public boolean isDraftOwner(Long draftId, String currentLogin) {
        return draftRepository.findById(draftId)
                .map(draft -> draft.getUser() != null && currentLogin.equals(draft.getUser().getLogin()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isSelfUser(Long userId, String currentLogin) {
        if (userId == null || currentLogin == null) return false;
        return userRepository.findById(userId)
                .map(user -> currentLogin.equals(user.getLogin()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isPlaceOwner(Long placeId, String currentLogin) {
        if (placeId == null || currentLogin == null) return false;
        return placeRepository.findById(placeId)
                .map(place -> place.getOwner() != null && currentLogin.equals(place.getOwner().getLogin()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isDraftPlaceOwner(Long draftId, String currentLogin) {
        if (draftId == null || currentLogin == null) return false;
        return draftRepository.findById(draftId)
                .map(draft -> draft.getPlace() != null &&
                        draft.getPlace().getOwner() != null &&
                        currentLogin.equals(draft.getPlace().getOwner().getLogin()))
                .orElse(false);
    }
}