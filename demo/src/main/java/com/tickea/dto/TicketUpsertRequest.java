package com.tickea.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)//esto es un salvoconducto por si nos llegan datos de mas que no pete. que ignore y siga
public class TicketUpsertRequest {
	private String fecha;
	private String uidUsuario;
	
    @JsonAlias({"textoBruto","texto","textoOCR","textFilas"})
    private String textoFilas;  // solo para UiPath
	
	public TicketUpsertRequest() {
		super();
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTextoFilas() {
		return textoFilas;
	}

	public void setTextoFilas(String textoFilas) {
		this.textoFilas = textoFilas;
	}

	public String getUidUsuario() {
		return uidUsuario;
	}

	public void setUidUsuario(String uidUsuario) {
		this.uidUsuario = uidUsuario;
	}
}
