package com.tickea.jpa;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @Column(name = "firebase_uid", nullable = false, length = 128)
    private String firebaseUid;
    
    @Column(name = "fecha_ticket", nullable = false)
    private LocalDate fechaTicket;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
	public Long getId() {
		return id;
	}

	public String getFirebaseUid() {
		return firebaseUid;
	}

	public void setFirebaseUid(String firebaseUid) {
		this.firebaseUid = firebaseUid;
	}

	public LocalDate getFechaTicket() {
		return fechaTicket;
	}

	public void setFechaTicket(LocalDate fechaTicket) {
		this.fechaTicket = fechaTicket;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}
}


