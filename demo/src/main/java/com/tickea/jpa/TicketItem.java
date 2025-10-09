package com.tickea.jpa;

import java.math.BigDecimal;
import java.sql.Timestamp;
import jakarta.persistence.*;


@Entity
@Table(name = "ticket_items")
public class TicketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
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

