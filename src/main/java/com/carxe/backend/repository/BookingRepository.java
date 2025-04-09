package com.carxe.backend.repository;

import com.carxe.backend.model.Booking;
import com.carxe.backend.model.Car;
import com.carxe.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByCarId(Long carId);
    List<Booking> findByStatus(String status);

    @Query("SELECT b FROM Booking b WHERE " +
           "b.car.id = :carId AND " +
           "b.status NOT IN ('CANCELLED', 'REJECTED') AND " +
           "((b.startDate BETWEEN :startDate AND :endDate) OR " +
           "(b.endDate BETWEEN :startDate AND :endDate) OR " +
           "(:startDate BETWEEN b.startDate AND b.endDate))")
    List<Booking> findOverlappingBookings(
        @Param("carId") Long carId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<Booking> findByUserAndCarAndStatus(
        User user, 
        Car car, 
        String status
    );
}
