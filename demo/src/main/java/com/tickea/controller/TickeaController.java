package com.tickea.controller;

import com.tickea.jpa.*;
import com.tickea.dto.TicketResponse;
import com.tickea.dto.TicketUpsertRequest;
import com.tickea.repository.TicketRepository;
import com.tickea.service.TickeaService;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> crearTicket(@RequestBody TicketUpsertRequest nuevoTicket) {
        TicketResponse resultado = tickeaService.procesarTicket(nuevoTicket);
        //Devolvemos respuesta OK (200)
        return ResponseEntity.ok(resultado);
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
