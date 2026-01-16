package com.assignment.ride_hailing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AuditRecord {

    @CreatedDate
    @Column(name = "CREATED_DATE",nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE",nullable = false)
    private Instant updatedDate;
}
