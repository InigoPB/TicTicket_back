package com.tickea.JPA;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(columnDefinition = "TEXT") 
    private String textotickets;

    public Long getId() {
        return id;
    }

    public String getTextotickets() {
        return textotickets;
    }

    public void setTextotickets(String textotickets) {
        this.textotickets = textotickets;
    }
}
