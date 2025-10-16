package com.tickea.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tickea.jpa.*;
import com.tickea.dto.TicketItemResponse;
import com.tickea.dto.TicketResponse;
import com.tickea.dto.TicketUpsertRequest;
import com.tickea.repository.TicketItemRepository;
import com.tickea.repository.TicketRepository;

import jakarta.transaction.Transactional;

@Service
public class TickeaService {
	private final TicketRepository ticketRepository;
	private final TicketItemRepository ticketItemRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	// URL y token para UiPath Orchestrator. IÑIGO mira a ver si los traes del
	// config quedaria wapote
	private static final String UIPATH_URL = "https://cloud.uipath.com/tickea/DefaultTenant/orchestrator_/t/3f146a50-d8ba-4f9d-b77c-99bafb2eb3c4/TickeaDataExtractor";
	private static final String BEARER_TOKEN = "rt_3FD97674A4782B2467FB50113A7EA8DA80721642C6D8C50B29D2482B4255AF62-1";
	private static final String UIPATH_URL_BASE = "https://cloud.uipath.com/tickea/DefaultTenant/orchestrator_";

	public TickeaService(TicketRepository ticketRepository, TicketItemRepository ticketItemRepository) {
		super();
		this.ticketRepository = ticketRepository;
		this.ticketItemRepository = ticketItemRepository;
	}

	public String StartJob(String textoFilas) throws Exception {
    final int TIMEOUT_MS = 300_000; // 5 minutos máximo de espera
    final int POLL_INTERVAL_MS = 5000; // cada 5 segundos

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + BEARER_TOKEN);

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

    // Convertir textoFilas a JSON
    ObjectNode bodyJson = mapper.createObjectNode();
    bodyJson.put("texto_ocr", textoFilas);

    HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(bodyJson), headers);

    // Lanzar el job
    ResponseEntity<String> postResponse = restTemplate.exchange(UIPATH_URL, HttpMethod.POST, request, String.class);

    if (!postResponse.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException("Error al lanzar el job: " + postResponse.getBody());
    }

    JsonNode postRoot = mapper.readTree(postResponse.getBody());
    if (!postRoot.has("id")) {
        throw new RuntimeException("Respuesta inválida al lanzar el job: " + postResponse.getBody());
    }

    long jobId = postRoot.get("id").asLong();
    String jobUrl = UIPATH_URL_BASE + "/odata/Jobs(" + jobId + ")";

    // Esperar a que termine el job
    long startTime = System.currentTimeMillis();
    while (true) {
        ResponseEntity<String> getResponse = restTemplate.exchange(jobUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        if (!getResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al consultar el job: " + getResponse.getBody());
        }

        JsonNode jobData = mapper.readTree(getResponse.getBody());
        String state = jobData.path("State").asText();

        switch (state) {
            case "Pending":
            case "Running":
                if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
                    throw new RuntimeException("Timeout: el job no terminó en " + (TIMEOUT_MS / 1000) + " segundos");
                }
                Thread.sleep(POLL_INTERVAL_MS);
                break;
            case "Successful":
                return getResponse.getBody(); // job completado correctamente
            case "Faulted":
            case "Stopped":
                throw new RuntimeException("Job terminó con error: " + getResponse.getBody());
            default:
                throw new RuntimeException("Estado desconocido del job: " + state);
        }
    }
}


	public TicketResponse procesarTicketUiPath(String uidUsuario, String fecha, List<Map<String, Object>> productos) {
		// 1️⃣ Crear ticket en la tabla tickets
		Ticket ticket = new Ticket();
		ticket.setFirebaseUid(uidUsuario);
		ticket.setFechaTicket(LocalDate.parse(fecha));
		ticket = ticketRepository.save(ticket); // Inserta en tabla tickets

		// 2️⃣ Crear items en la tabla ticket_items
		List<TicketItemResponse> itemsResponse = new ArrayList<>();
		for (Map<String, Object> p : productos) {
			TicketItem item = new TicketItem();
			item.setTicket(ticket); // Relación con el ticket creado
			item.setNombreProducto((String)p.get("nombre"));
			item.setCodigoProducto((String)p.get("codigo"));

			// Aseguramos que los campos numéricos sean Number y no String
			item.setOperaciones(p.get("operaciones") != null 
				? ((Number)p.get("operaciones")).intValue() 
				: 0);
			item.setTotalImporte(p.get("total_importe") != null 
				? BigDecimal.valueOf(((Number)p.get("total_importe")).doubleValue()) 
				: BigDecimal.ZERO);
			item.setPeso(p.get("peso") != null 
				? BigDecimal.valueOf(((Number)p.get("peso")).doubleValue()) 
				: BigDecimal.ZERO);
			item.setUnidades(p.get("unidades") != null 
				? BigDecimal.valueOf(((Number)p.get("unidades")).doubleValue()) 
				: BigDecimal.ZERO);

			ticketItemRepository.save(item); // Inserta en tabla ticket_items

			// Agregamos al DTO de respuesta
			TicketItemResponse responseItem = new TicketItemResponse();
			responseItem.setNombre(item.getNombreProducto());
			responseItem.setCodigo(item.getCodigoProducto());
			responseItem.setOperaciones(item.getOperaciones());
			responseItem.setImporteTotal(item.getTotalImporte().doubleValue());
			responseItem.setPeso(item.getPeso().doubleValue());
			responseItem.setUnidades(item.getUnidades().intValue());
			itemsResponse.add(responseItem);
		}

		// 3️⃣ Crear DTO de respuesta
		TicketResponse ticketResponse = new TicketResponse();
		ticketResponse.setId(ticket.getId());
		ticketResponse.setFecha(ticket.getFechaTicket().toString());
		ticketResponse.setUidUsuario(uidUsuario);
		ticketResponse.setProductos(itemsResponse);

		return ticketResponse;
	}


    public List<LocalDate> listarFechasRegistradas(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listarFechasRegistradas'");
    }

}
