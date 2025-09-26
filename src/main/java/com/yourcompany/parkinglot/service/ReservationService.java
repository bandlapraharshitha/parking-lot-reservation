package com.yourcompany.parkinglot.service;

import com.yourcompany.parkinglot.dto.ReservationRequest;
import com.yourcompany.parkinglot.exception.ResourceNotFoundException;
import com.yourcompany.parkinglot.exception.ValidationException;
import com.yourcompany.parkinglot.model.Reservation;
import com.yourcompany.parkinglot.model.Slot;
import com.yourcompany.parkinglot.model.VehicleType;
import com.yourcompany.parkinglot.repository.ReservationRepository;
import com.yourcompany.parkinglot.repository.SlotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Transactional
    public Reservation reserveSlot(ReservationRequest request) {
        validateReservationRequest(request);

        // Find the slot
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id " + request.getSlotId()));

        // Check for availability (no overlapping reservations)
        List<Reservation> existingReservations = reservationRepository.findOverlappingReservations(
                request.getSlotId(), request.getStartTime(), request.getEndTime());
        
        if (!existingReservations.isEmpty()) {
            throw new ValidationException("Slot is already reserved for the given time range.");
        }

        // Calculate cost
        double cost = calculateCost(request.getStartTime(), request.getEndTime(), request.getVehicleType());

        // Create and save reservation
        Reservation reservation = new Reservation();
        reservation.setSlot(slot);
        reservation.setVehicleNumber(request.getVehicleNumber());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setCost(cost);

        return reservationRepository.save(reservation);
    }

    private void validateReservationRequest(ReservationRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new ValidationException("Start time must be before end time.");
        }

        long durationInHours = ChronoUnit.HOURS.between(request.getStartTime(), request.getEndTime());
        if (durationInHours > 24) {
            throw new ValidationException("Reservation duration cannot exceed 24 hours.");
        }
        
        // Vehicle number format validation (XX00XX0000)
        String regex = "^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$";
        if (!request.getVehicleNumber().matches(regex)) {
            throw new ValidationException("Vehicle number format is invalid. Expected format: XX00XX0000.");
        }
    }
    
    // Method to calculate cost
    private double calculateCost(LocalDateTime start, LocalDateTime end, VehicleType vehicleType) {
        long minutes = ChronoUnit.MINUTES.between(start, end);
        long hours = (long) Math.ceil(minutes / 60.0);
        return hours * vehicleType.getHourlyRate();
    }
    
    // Other methods for fetching, listing, and canceling reservations
    public Optional<Reservation> getReservationDetails(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public void cancelReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with id " + id);
        }
        reservationRepository.deleteById(id);
    }
    
    public List<Slot> getAvailableSlots(LocalDateTime start, LocalDateTime end) {
        // Query for all slots
        List<Slot> allSlots = slotRepository.findAll();

        // Find occupied slots for the given time range
        List<Long> occupiedSlotIds = reservationRepository.findOccupiedSlotIds(start, end);

        // Filter out the occupied slots
        return allSlots.stream()
                .filter(slot -> !occupiedSlotIds.contains(slot.getId()))
                .collect(Collectors.toList());
    }
}