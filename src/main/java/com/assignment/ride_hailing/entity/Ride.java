package com.assignment.ride_hailing.entity;

import com.assignment.ride_hailing.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Driver;

@Entity
@Table(name = "ride")
@Getter
@Setter
@NoArgsConstructor
public class Ride extends AuditRecord{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "RIDER",nullable = false)
    private Long riderId;

    @Column(name = "DRIVER")
    private Long driverId;

    @Column(name = "OTP")
    private Integer otp;

    @Column(name = "PICK_UP_LAT",nullable = false)
    private double pickupLatitude;

    @Column(name = "PICK_UP_LONG",nullable = false)
    private double pickupLongitude;

    @Column(name = "DESTINATION_LAT",nullable = false)
    private double destinationLatitude;

    @Column(name = "DESTINATION_LONG",nullable = false)
    private double destinationLongitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RideStatus status;

    @Column(name = "FARE")
    private Double fare;

    @Column(name = "SURGE_MULTIPLIER",nullable = false)
    private double surgeMultiplier;
}
