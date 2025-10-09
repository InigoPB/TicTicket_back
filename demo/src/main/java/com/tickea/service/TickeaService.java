package com.tickea.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TickeaService {

    private static final String UIPATH_URL = "https://cloud.uipath.com/tickea/DefaultTenant/orchestrator_/t/e80665ba-4f97-4190-b841-34cf16f6d155/Tickea_Orchestrator";
    private static final String BEARER_TOKEN = "rt_3FD97674A4782B2467FB50113A7EA8DA80721642C6D8C50B29D2482B4255AF62-1";

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
}
