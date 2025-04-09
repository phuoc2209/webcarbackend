package com.carxe.backend.repository;

import com.carxe.backend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByAvailableTrue();
    List<Car> findByBrandContainingOrModelContaining(String brand, String model);
    
    @Query(value = "SELECT * FROM cars ORDER BY id ASC LIMIT 6", nativeQuery = true)
    List<Car> findFirst6Cars();
    
    List<Car> findByIsFeaturedTrue();
}
