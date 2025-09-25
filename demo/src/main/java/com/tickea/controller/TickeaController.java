package com.tickea.controller;

import com.tickea.JPA.Ticket;
import com.tickea.repository.TickeaRepository;
import com.tickea.service.TickeaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/tickea")
public class TickeaController {

    private final TickeaService uiPathService;
    private final TickeaRepository tickeaRepository;

    // Constructor con @Autowired (opcional en Spring Boot 6+)
    public TickeaController(TickeaService uiPathService, TickeaRepository tickeaRepository) {
        this.uiPathService = uiPathService;
        this.tickeaRepository = tickeaRepository;
    }

    @PostMapping("/post-data-and-start-job")
    public ResponseEntity<String> guardarYStartJob(@RequestBody Ticket ticket) {
        // 1. Guardar en la base de datos
        Ticket saved = tickeaRepository.save(ticket);

        // 2. Lanzar proceso en UiPath
        String result = uiPathService.startJob();

        // 3. Responder al cliente
        return ResponseEntity.ok(
            "Ticket guardado con id " + saved.getId() + 
            " Job lanzado: " + result
        );
    }

    @GetMapping("/get-ticket-text")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
}
