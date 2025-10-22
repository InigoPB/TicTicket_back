package com.tickea.dto;

public class TicketItemResponse {
    private String nombre;
    private String codigo;
    private int unidades;
	private int operaciones;
    private double peso;
    private double importeTotal;
    
    
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public int getUnidades() {
		return unidades;
	}
	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public double getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(double importeTotal) {
		this.importeTotal = importeTotal;
	}
	public int getOperaciones() {
		return operaciones;
	}
	public void setOperaciones(int operaciones) {
		this.operaciones = operaciones;
	}
    
    

}
