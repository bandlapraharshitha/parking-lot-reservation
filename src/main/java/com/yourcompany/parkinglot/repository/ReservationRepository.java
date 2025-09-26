package com.yourcompany.parkinglot.repository;

import com.yourcompany.parkinglot.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.slot.id = :slotId AND " +
           "((:startTime BETWEEN r.startTime AND r.endTime) OR " +
           "(:endTime BETWEEN r.startTime AND r.endTime) OR " +
           "(r.startTime BETWEEN :startTime AND :endTime) OR " +
           "(r.endTime BETWEEN :startTime AND :endTime))")
    List<Reservation> findOverlappingReservations(Long slotId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT r.slot.id FROM Reservation r WHERE " +
           "((:startTime BETWEEN r.startTime AND r.endTime) OR " +
           "(:endTime BETWEEN r.startTime AND r.endTime) OR " +
           "(r.startTime BETWEEN :startTime AND :endTime) OR " +
           "(r.endTime BETWEEN :startTime AND :endTime))")
    List<Long> findOccupiedSlotIds(LocalDateTime startTime, LocalDateTime endTime);
}