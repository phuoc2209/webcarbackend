package com.carxe.backend.controller;

import com.carxe.backend.exception.ResourceNotFoundException;
import com.carxe.backend.model.Booking;
import com.carxe.backend.model.User;
import com.carxe.backend.payload.request.BookingRequest;
import com.carxe.backend.repository.UserRepository;
import com.carxe.backend.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId()));
    }

    @PostMapping(value = "/{bookingId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocuments(
            @PathVariable Long bookingId,
            @RequestPart("files") MultipartFile[] files) {
        return ResponseEntity.ok("Documents uploaded successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest bookingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
      Booking booking = bookingService.createBooking(bookingRequest, user);
      System.out.println("Created booking: " + booking);
      return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Booking booking = bookingService.getBookingById(id);
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only cancel your own bookings");
        }
        
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<Booking>> getBookingsByCarId(@PathVariable Long carId) {
        return ResponseEntity.ok(bookingService.getCarBookings(carId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Booking booking = bookingService.getBookingById(id);
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only cancel your own bookings");
        }
        
        bookingService.updateBookingStatus(id, "CANCELLED");
        return ResponseEntity.noContent().build();
    }
}
