package com.carxe.backend.service;

import com.carxe.backend.model.Car;
import java.time.LocalDate;
import java.util.List;

public interface CarService {
    List<Car> getAllCars();
    List<Car> getFeaturedCars();
    List<Car> searchCars(String query);
    Car getCarById(Long id);
    Car createCar(Car car);
    Car updateCar(Long id, Car car);
    void deleteCar(Long id);
    
    boolean checkAvailability(Long carId, LocalDate startDate, LocalDate endDate);
}
