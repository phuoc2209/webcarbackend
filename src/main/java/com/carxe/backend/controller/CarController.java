package com.carxe.backend.controller;

import com.carxe.backend.model.Car;
import com.carxe.backend.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", maxAge = 3600)
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Car>> getFeaturedCars() {
        System.out.println("Received request for featured cars");
        List<Car> featuredCars = carService.getFeaturedCars();
        System.out.println("Returning " + featuredCars.size() + " featured cars");
        return ResponseEntity.ok(featuredCars);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCars(@RequestParam String q) {
        return ResponseEntity.ok(carService.searchCars(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return ResponseEntity.ok(carService.createCar(car));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        return ResponseEntity.ok(carService.updateCar(id, car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(carService.checkAvailability(id, startDate, endDate));
    }
}
