//package com.blps_lab1.demo.services.utils;
//
//import com.blps_lab1.demo.dto.ReservationDto;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Component
//public class ReservationDraftStorage {
//
//    private final Map<Long, ReservationDto> drafts = new ConcurrentHashMap<>();
//    private final AtomicLong draftSequence = new AtomicLong(1);
//
//    public Long saveDraft(ReservationDto request) {
//        Long draftId = draftSequence.getAndIncrement();
//        drafts.put(draftId, request);
//        return draftId;
//    }
//
//    public ReservationDto getDraft(Long draftId) {
//        return drafts.get(draftId);
//    }
//
//    public void removeDraft(Long draftId) {
//        drafts.remove(draftId);
//    }
//}