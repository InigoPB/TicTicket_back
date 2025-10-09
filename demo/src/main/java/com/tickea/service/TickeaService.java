package com.tickea.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickea.jpa.*;
import com.tickea.dto.TicketItemResponse;
import com.tickea.dto.TicketResponse;
import com.tickea.dto.TicketUpsertRequest;
import com.tickea.repository.TicketItemRepository;
import com.tickea.repository.TicketRepository;

@Service
public class TickeaService {
    private final TicketRepository ticketRepository;
    private final TicketItemRepository ticketItemRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    // URL y token para UiPath Orchestrator. IÑIGO mira a ver si los traes del config quedaria wapote
    private static final String UIPATH_URL = "https://cloud.uipath.com/tickea/DefaultTenant/orchestrator_/t/e80665ba-4f97-4190-b841-34cf16f6d155/Tickea_Orchestrator";
    private static final String BEARER_TOKEN = "rt_3FD97674A4782B2467FB50113A7EA8DA80721642C6D8C50B29D2482B4255AF62-1";

	public TickeaService(TicketRepository ticketRepository, TicketItemRepository ticketItemRepository) {
		super();
		this.ticketRepository = ticketRepository;
		this.ticketItemRepository = ticketItemRepository;
	}

	public String startJob() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + BEARER_TOKEN);

        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                UIPATH_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        return response.getBody();
    }
	
	public TicketResponse procesarTicket(TicketUpsertRequest peticion) {
	    //Crear Ticket concorde a la tabla real
	    Ticket ticket = new Ticket();
	    ticket.setFirebaseUid(peticion.getUidUsuario());
	    ticket.setFechaTicket(parseFechaISO(peticion.getFecha())); // "2025-10-06" -> LocalDate
	    
	    Ticket saved = ticketRepository.save(ticket); // INSERT en tickets
	    
	    //Llamada a UiPath (stub mientras tanto)
	    String resultadoJSON = llamarUiPathStub(peticion.getTextoFilas());

	    //Parsear productos y guardar en ticket_items
	    List<TicketItem> ticketItems = new ArrayList<>();
	    List<TicketItemResponse> ticketItemsResponse = new ArrayList<>();
	    
	    if (resultadoJSON != null && !resultadoJSON.isBlank()) {
	        try {
	            ObjectMapper mapeador = new ObjectMapper();
	            JsonNode root = mapeador.readTree(resultadoJSON);
	            JsonNode arrProd = (root != null) ? root.get("productos") : null;

	            if (arrProd != null && arrProd.isArray()) {
	                for (JsonNode producto : arrProd) {

	                    //TicketItem (Entidad JPA)
	                    TicketItem item = new TicketItem();
	                    item.setTicket(saved);

	                    //codigo_producto
	                    item.setCodigoProducto(
	                        (producto.hasNonNull("codigo")) ? producto.get("codigo").asText() : ""
	                    );

	                    //nombre_producto
	                    item.setNombreProducto(
	                        (producto.hasNonNull("nombre")) ? producto.get("nombre").asText() : null
	                    );

	                    //operaciones
	                    item.setOperaciones(
	                        (producto.hasNonNull("operaciones")) ? producto.get("operaciones").asInt() : 0
	                    );

	                    //total_importe
	                    item.setTotalImporte(
	                        (producto.hasNonNull("importeTotal"))
	                            ? new java.math.BigDecimal(producto.get("importeTotal").asText())
	                            : BigDecimal.ZERO
	                    );

	                    //peso
	                    item.setPeso(
	                        (producto.hasNonNull("peso"))
	                            ? new java.math.BigDecimal(producto.get("peso").asText())
	                            : BigDecimal.ZERO
	                    );

	                    //unidades
	                    item.setUnidades(
	                        (producto.hasNonNull("unidades"))
	                            ? new java.math.BigDecimal(producto.get("unidades").asText())
	                            : BigDecimal.ZERO
	                    );

	                    ticketItems.add(item);

	                    //TicketItemResponse (DTO de salida)
	                    TicketItemResponse dto = new TicketItemResponse();
	                    dto.setCodigo( (producto.hasNonNull("codigo")) ? producto.get("codigo").asText() : "" );
	                    dto.setNombre( (producto.hasNonNull("nombre")) ? producto.get("nombre").asText() : "" );
	                    dto.setUnidades( (producto.hasNonNull("unidades")) ? producto.get("unidades").asInt() : 0 );
	                    dto.setPeso( (producto.hasNonNull("peso")) ? producto.get("peso").asDouble() : 0.0 );
	                    dto.setImporteTotal( (producto.hasNonNull("importeTotal")) ? producto.get("importeTotal").asDouble() : 0.0 );
	                    ticketItemsResponse.add(dto);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    if (!ticketItems.isEmpty()) {
	        ticketItemRepository.saveAll(ticketItems);
	    }

	    TicketResponse resp = new TicketResponse();
	    resp.setId(saved.getId());
	    resp.setFecha(peticion.getFecha());
	    resp.setUidUsuario(peticion.getUidUsuario());
	    resp.setProductos(ticketItemsResponse);
	    
	    return resp;
	}
	
	private LocalDate parseFechaISO(String s) {
	    return LocalDate.parse(s); // formato yyyy-MM-dd
	}
	
	private String llamarUiPathStub(String texto) {
	    // [Integración real UiPath AQUÍ] -> mientras, stub para validar inserts
	    return """
	      {
	        "productos": [
	          { "nombre":"CAMARONES", "codigo":"56557", "operaciones":1, "importeTotal":21.36, "peso":2.500, "unidades":2 },
	          { "nombre":"PEPETES JL 400/600", "codigo":"888432", "operaciones":10, "importeTotal":999.90, "peso":11.800, "unidades":13 }
	        ]
	      }
	    """;
	}
	
}
