package com.tickea.controller;

import com.tickea.jpa.*;
import com.tickea.dto.TicketResponse;
import com.tickea.dto.TicketUpsertRequest;
import com.tickea.repository.TicketRepository;
import com.tickea.service.TickeaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
            @RequestBody List<Map<String, Object>> productos) {

        // Llamamos al servicio que procesa y guarda el ticket
        TicketResponse response = tickeaService.procesarTicketUiPath(uidUsuario, fecha, productos);
        return ResponseEntity.ok(response);
    }




    @GetMapping("/get-ticket-text")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @GetMapping ("/fechas-registradas")
    public List<LocalDate> fechasRegistradas(@RequestParam String uid){
    	return tickeaService.listarFechasRegistradas(uid);
    }
    
}
