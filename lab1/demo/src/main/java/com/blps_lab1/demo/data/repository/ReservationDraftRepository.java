package com.blps_lab1.demo.data.repository;

import com.blps_lab1.demo.data.tables.ReservationDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationDraftRepository extends JpaRepository<ReservationDraft, Long> {
    void deleteByCreatedAtBefore(LocalDateTime time);

}
