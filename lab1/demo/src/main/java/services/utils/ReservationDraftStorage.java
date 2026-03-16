package services.utils;

import dto.ReservationRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReservationDraftStorage {

    private final Map<Long, ReservationRequest> drafts = new ConcurrentHashMap<>();

    public Long saveDraft(ReservationRequest request) {
        Long draftId = request.getIdOwner();
        drafts.put(draftId, request);
        return draftId;
    }

    public ReservationRequest getDraft(Long draftId) {
        return drafts.get(draftId);
    }

    public void removeDraft(Long draftId) {
        drafts.remove(draftId);
    }
}