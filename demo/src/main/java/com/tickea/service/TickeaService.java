package com.tickea.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TickeaService {

    // Valores fijos del proceso
    private static final String UIPATH_URL = "https://cloud.uipath.com/tickea/DefaultTenant/orchestrator_/odata/Jobs/UiPath.Server.Configuration.OData.StartJobs";
    private static final String FOLDER_KEY = "e80665ba-4f97-4190-b841-34cf16f6d155";
    private static final String BEARER_TOKEN = "rt_3FD97674A4782B2467FB50113A7EA8DA80721642C6D8C50B29D2482B4255AF62-1";
    private static final String RELEASE_KEY = "15d3fdd2-14e5-42ba-b19f-f2cbf24a5168";
    private static final Long ROBOT_ID = 2136589L;

    public String startJob() {
        // Configuración de headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-UIPATH-FolderKey", FOLDER_KEY);
        headers.set("Authorization", "Bearer " + BEARER_TOKEN);

        // Cuerpo del POST
        String body = """
        {
            "startInfo": {
            "ReleaseKey": "15d3fdd2-14e5-42ba-b19f-f2cbf24a5168",
            "Strategy": "Specific",
            "RobotIds": [2136589],
            "InputArguments": "{}"
            }
        }
        """.formatted(RELEASE_KEY, ROBOT_ID);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // Llamada POST a UiPath Orchestrator
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
