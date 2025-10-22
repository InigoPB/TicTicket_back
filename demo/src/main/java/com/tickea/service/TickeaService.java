package com.tickea.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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


 	@Transactional
    public TicketResponse procesarTicketUiPath(String uidUsuario, String fecha, String productosJson) {
        // Parsear el JSON usando org.json
        JSONArray productosArray = new JSONArray(productosJson);

        // 1️⃣ Crear ticket
        Ticket ticket = new Ticket();
        ticket.setFirebaseUid(uidUsuario);
        ticket.setFechaTicket(LocalDate.parse(fecha));
        ticket = ticketRepository.save(ticket);

        // 2️⃣ Crear items
        List<TicketItemResponse> itemsResponse = new ArrayList<>();
        for (int i = 0; i < productosArray.length(); i++) {
            JSONObject p = productosArray.getJSONObject(i);

            TicketItem item = new TicketItem();
            item.setTicket(ticket);
            item.setNombreProducto(p.getString("nombre"));
            item.setCodigoProducto(p.getString("codigo"));

            item.setOperaciones(p.has("operaciones") && !p.isNull("operaciones") ? p.getInt("operaciones") : 0);
            item.setTotalImporte(p.has("total_importe") && !p.isNull("total_importe") ? BigDecimal.valueOf(p.getDouble("total_importe")) : BigDecimal.ZERO);
            item.setPeso(p.has("peso") && !p.isNull("peso") ? BigDecimal.valueOf(p.getDouble("peso")) : BigDecimal.ZERO);
            item.setUnidades(p.has("unidades") && !p.isNull("unidades") ? BigDecimal.valueOf(p.getDouble("unidades")) : BigDecimal.ZERO);

            ticketItemRepository.save(item);

            // DTO de respuesta
            TicketItemResponse responseItem = new TicketItemResponse();
            responseItem.setNombre(item.getNombreProducto());
            responseItem.setCodigo(item.getCodigoProducto());
            responseItem.setOperaciones(item.getOperaciones());
            responseItem.setImporteTotal(item.getTotalImporte().doubleValue());
            responseItem.setPeso(item.getPeso().doubleValue());
            responseItem.setUnidades(item.getUnidades().intValue());
            itemsResponse.add(responseItem);
        }

        // 3️⃣ DTO de respuesta
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setFecha(ticket.getFechaTicket().toString());
        ticketResponse.setUidUsuario(uidUsuario);
        ticketResponse.setProductos(itemsResponse);

        return ticketResponse;
    }




    public List<TicketItem> getItemsByFechaAndUid(LocalDate fecha, String uid) {
        return ticketItemRepository.findItemsByFechaAndUid(fecha, uid);
    }
        public List<TicketItem> getItemsByFechaRangeAndUid(LocalDate fechaInicio, LocalDate fechaFin, String uid) {
        return ticketItemRepository.findItemsByFechaRangeAndUid(fechaInicio, fechaFin, uid);
    }
     public List<TicketItem> getItemsByFechaRangeAndUidAggregated(LocalDate fechaInicio, LocalDate fechaFin, String uid) {
        List<TicketItem> items = ticketItemRepository.findItemsByFechaRangeAndUid(fechaInicio, fechaFin, uid);

        // Crear lista para resultados finales sumando por codigoProducto
        List<TicketItem> result = new ArrayList<>();

        for (TicketItem item : items) {
            boolean found = false;
            for (TicketItem existing : result) {
                if (existing.getCodigoProducto().equals(item.getCodigoProducto())) {
                    existing.setOperaciones(existing.getOperaciones() + item.getOperaciones());
                    existing.setTotalImporte(existing.getTotalImporte().add(item.getTotalImporte()));
                    existing.setPeso(existing.getPeso().add(item.getPeso()));
                    existing.setUnidades(existing.getUnidades().add(item.getUnidades()));
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(new TicketItem(
                        item.getId(),
                        item.getCodigoProducto(),
                        item.getNombreProducto(),
                        item.getOperaciones(),
                        item.getTotalImporte(),
                        item.getPeso(),
                        item.getUnidades(),
                        item.getCreatedAt()
                ));
            }
        }

        return result;
    }

     public String getFechasRegistradas(String uid) {
        // TODO Auto-generated method stub
        return ticketRepository.findFechasRegistradas(uid).toString();
     }
}
