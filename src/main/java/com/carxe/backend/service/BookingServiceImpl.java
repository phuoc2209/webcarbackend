package com.carxe.backend.service;

import com.carxe.backend.exception.ResourceNotFoundException;
import com.carxe.backend.model.Booking;
import com.carxe.backend.model.Car;
import com.carxe.backend.model.User;
import com.carxe.backend.payload.request.BookingRequest;
import com.carxe.backend.repository.BookingRepository;
import com.carxe.backend.repository.CarRepository;
import com.carxe.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Override
    public List<Booking> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return bookingRepository.findByUser(user);
    }

    @Override
    public List<Booking> getCarBookings(Long carId) {
        return bookingRepository.findByCarId(carId);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    @Override
    public Booking createBooking(BookingRequest bookingRequest, User user) {
        Car car = carRepository.findById(bookingRequest.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        // Check for approved overlapping bookings
        List<Booking> overlappingApprovedBookings = bookingRepository.findOverlappingBookings(
            car.getId(),
            bookingRequest.getStartDate(),
            bookingRequest.getEndDate()
        ).stream()
        .filter(b -> "APPROVED".equals(b.getStatus()))
        .toList();

        if (!overlappingApprovedBookings.isEmpty()) {
            throw new IllegalStateException("Car is already booked for selected dates");
        }

        // Check for pending bookings by same user
        List<Booking> userPendingBookings = bookingRepository.findByUserAndCarAndStatus(
            user, 
            car,
            "PENDING"
        );
        
        if (!userPendingBookings.isEmpty()) {
            throw new IllegalStateException(
                "Bạn đã có một đơn đặt xe đang chờ xử lý cho xe này. " +
                "Vui lòng hủy đơn đặt trước hoặc chờ xử lý xong."
            );
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setStartDate(bookingRequest.getStartDate());
        booking.setEndDate(bookingRequest.getEndDate());
        booking.setStatus("PENDING");
        booking.setNotes(bookingRequest.getNotes());
        
        // Calculate total price
        long days = bookingRequest.getEndDate().toEpochDay() - bookingRequest.getStartDate().toEpochDay();
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));
        booking.setTotalPrice(totalPrice);
        
        // Update car availability - set to false for PENDING bookings
        car.setAvailable(false);
        carRepository.save(car);
        
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = getBookingById(id);
        booking.setStatus(status);
        
        // If booking is cancelled or rejected, make car available again
        if ("CANCELLED".equals(status) || "REJECTED".equals(status)) {
            Car car = booking.getCar();
            car.setAvailable(true);
            carRepository.save(car);
        }
        
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        
        // Only allow cancelling PENDING bookings
        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException("Only PENDING bookings can be cancelled");
        }
        
        // Make car available again
        Car car = booking.getCar();
        car.setAvailable(true);
        carRepository.save(car);
        
        bookingRepository.delete(booking);
    }
}
