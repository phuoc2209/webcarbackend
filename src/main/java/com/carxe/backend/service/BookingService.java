package com.carxe.backend.service;
import com.carxe.backend.model.Booking;
import com.carxe.backend.model.User;
import com.carxe.backend.payload.request.BookingRequest;
import java.util.List;

public interface BookingService {
    List<Booking> getUserBookings(Long userId);
    List<Booking> getCarBookings(Long carId);
    Booking getBookingById(Long id);
    Booking createBooking(BookingRequest bookingRequest, User user);
    Booking updateBookingStatus(Long id, String status);
    void deleteBooking(Long id);
}
