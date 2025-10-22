package com.tickea.controller;

import com.tickea.jpa.*;
import com.tickea.dto.TicketResponse;
import com.tickea.dto.TicketUpsertRequest;
import com.tickea.repository.TicketRepository;
import com.tickea.service.TickeaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@CrossOrigin(origins = "*") //para pruebas
@RestController
@RequestMapping("/tickea")
public class TickeaController {

    private final TickeaService tickeaService;

    //Inyección de dependencias
    public TickeaController(TickeaService tickeaService) {
        this.tickeaService = tickeaService;
    }
    @PostMapping("/StartJob")
    public ResponseEntity<String> startJob(@RequestBody String entity) {
        try {
            // Llama al servicio y obtiene la respuesta final del job
            String jobResult = tickeaService.StartJob(entity);

            // Devuelve la respuesta completa del job con HTTP 200
            return ResponseEntity.ok(jobResult);
        } catch (Exception e) {
            // Devuelve un mensaje de error con HTTP 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al ejecutar el job: " + e.getMessage());
        }
    }

    

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> recibirTicketUiPath(
            @RequestParam String uidUsuario,
            @RequestParam String fecha,
            @RequestBody String productosJson) {

        // Llamamos al servicio que procesa y guarda el ticket
        // El servicio ahora se encargará de parsear el JSON string
        TicketResponse response = tickeaService.procesarTicketUiPath(uidUsuario, fecha, productosJson);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fechas-registradas")
    public String getFechasRegistradas(@RequestParam String uid) {
        return tickeaService.getFechasRegistradas(uid);
    }
    

    @GetMapping("/ticket-items")
    public List<TicketItem> getTicketItems(@RequestParam("fecha") String fecha,
                                           @RequestParam("uid") String uid) {
        LocalDate fechaTicket = LocalDate.parse(fecha);
        return tickeaService.getItemsByFechaAndUid(fechaTicket, uid);
    }
        @GetMapping("/ticket-items/rango")
    public List<TicketItem> getTicketItemsRango(@RequestParam("fechaInicio") String fechaInicio,
                                                @RequestParam("fechaFin") String fechaFin,
                                                @RequestParam("uid") String uid) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        return tickeaService.getItemsByFechaRangeAndUidAggregated(inicio, fin, uid);
    }
}
