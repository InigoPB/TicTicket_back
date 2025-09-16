package com.tickea.controller;

import com.tickea.service.TickeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uipath")
public class TickeaController {

    @Autowired
    private TickeaService uiPathService;

    @PostMapping("/start-job")
    public ResponseEntity<String> startJob() {
        String result = uiPathService.startJob();
        return ResponseEntity.ok(result);
    }
}
