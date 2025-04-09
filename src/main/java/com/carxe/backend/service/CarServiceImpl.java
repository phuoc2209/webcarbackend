package com.carxe.backend.service;

import com.carxe.backend.exception.ResourceNotFoundException;
import com.carxe.backend.model.Booking;
import com.carxe.backend.model.Car;
import com.carxe.backend.repository.BookingRepository;
import com.carxe.backend.repository.CarRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> getFeaturedCars() {
        return carRepository.findFirst6Cars();
    }

    @Override
    public List<Car> searchCars(String query) {
        return carRepository.findByBrandContainingOrModelContaining(query, query);
    }

    @Override
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
    }

    @Override
    public Car createCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Car updateCar(Long id, Car carDetails) {
        Car car = getCarById(id);
        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setYear(carDetails.getYear());
        car.setColor(carDetails.getColor());
        car.setLicensePlate(carDetails.getLicensePlate());
        car.setPricePerDay(carDetails.getPricePerDay());
        car.setAvailable(carDetails.getAvailable());
        car.setDescription(carDetails.getDescription());
        car.setImageUrl(carDetails.getImageUrl());
        car.setIsFeatured(carDetails.getIsFeatured());
        return carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        Car car = getCarById(id);
        carRepository.delete(car);
    }

    @Override
    public boolean checkAvailability(Long carId, LocalDate startDate, LocalDate endDate) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
            carId,
            startDate,
            endDate
        );
        return overlappingBookings.isEmpty();
    }
}
