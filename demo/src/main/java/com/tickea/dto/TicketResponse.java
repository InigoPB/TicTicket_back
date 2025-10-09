package com.tickea.dto;

import java.util.List;

public class TicketResponse {
	    private Long id;
	    private String fecha;
	    private String uidUsuario;
	    private List<TicketItemResponse> productos;
	    
		public TicketResponse() {
			super();
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getFecha() {
			return fecha;
		}

		public void setFecha(String fecha) {
			this.fecha = fecha;
		}

		public String getUidUsuario() {
			return uidUsuario;
		}

		public void setUidUsuario(String uidUsuario) {
			this.uidUsuario = uidUsuario;
		}

		public List<TicketItemResponse> getProductos() {
			return productos;
		}

		public void setProductos(List<TicketItemResponse> productos) {
			this.productos = productos;
		} 
	
	
	
}
