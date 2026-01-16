package com.assignment.ride_hailing.repository;

import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.enums.RideStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT r FROM Ride r WHERE r.id = :id")
    Optional<Ride> findById(Long id);

    List<Ride> findAllByStatus(RideStatus status);

}
