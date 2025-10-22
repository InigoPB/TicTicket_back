package com.tickea.jpa;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_items")
public class TicketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // evita problemas de serialización
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "codigo_producto", nullable = false, length = 50)
    private String codigoProducto;

    @Column(name = "nombre_producto", length = 120)
    private String nombreProducto;

    @Column(name = "operaciones")
    private Integer operaciones;

    @Column(name = "total_importe", precision = 12, scale = 2)
    private BigDecimal totalImporte;

    @Column(name = "peso", precision = 12, scale = 3)
    private BigDecimal peso;

    @Column(name = "unidades", precision = 12, scale = 3)
    private BigDecimal unidades;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    public TicketItem() {
        // constructor vacío
    }

    public TicketItem(Long id2, String codigoProducto2, String nombreProducto2, Integer operaciones2,
            BigDecimal totalImporte2, BigDecimal peso2, BigDecimal unidades2, Timestamp createdAt2) {
        this.id = id2;
        this.codigoProducto = codigoProducto2;
        this.nombreProducto = nombreProducto2;
        this.operaciones = operaciones2;
        this.totalImporte = totalImporte2;
        this.peso = peso2;
        this.unidades = unidades2;
        this.createdAt = createdAt2;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getOperaciones() {
        return operaciones;
    }

    public void setOperaciones(Integer operaciones) {
        this.operaciones = operaciones;
    }

    public BigDecimal getTotalImporte() {
        return totalImporte;
    }

    public void setTotalImporte(BigDecimal totalImporte) {
        this.totalImporte = totalImporte;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getUnidades() {
        return unidades;
    }

    public void setUnidades(BigDecimal unidades) {
        this.unidades = unidades;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
