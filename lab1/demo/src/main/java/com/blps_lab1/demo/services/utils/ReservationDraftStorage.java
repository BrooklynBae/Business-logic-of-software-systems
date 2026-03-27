package services.utils;

import dto.ReservationDto;
import dto.ReservationRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReservationDraftStorage {

    private final Map<Long, ReservationDto> drafts = new ConcurrentHashMap<>();

    public Long saveDraft(ReservationDto request) {
        Long draftId = request.getUser().getId();
        drafts.put(draftId, request);
        return draftId;
    }

    public ReservationDto getDraft(Long draftId) {
        return drafts.get(draftId);
    }

    public void removeDraft(Long draftId) {
        drafts.remove(draftId);
    }
}