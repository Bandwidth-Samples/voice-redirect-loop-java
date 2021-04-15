package com.bandwidth.controller;

import com.bandwidth.BandwidthClient;
import com.bandwidth.Environment;
import com.bandwidth.Main;
import com.bandwidth.exceptions.ApiException;
import com.bandwidth.voice.controllers.APIController;
import com.bandwidth.voice.models.ApiModifyCallRequest;
import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class EndpointController {

    Logger logger = LoggerFactory.getLogger(EndpointController.class);

    private final String username = System.getenv("BW_USERNAME");
    private final String password = System.getenv("BW_PASSWORD");
    private final String accountId = System.getenv("BW_ACCOUNT_ID");
    private final String baseUrl = System.getenv("BASE_CALLBACK_URL");

    private final BandwidthClient client = new BandwidthClient.Builder()
            .voiceBasicAuthCredentials(username, password)
            .environment(Environment.PRODUCTION)
            .build();

    private final APIController controller = client.getVoiceClient().getAPIController();


    @GetMapping("/activeCalls")
    public Set<String> getActiveCalls() {
        return Main.activeCalls;
    }

    @DeleteMapping("/calls/{callId}")
    public void modifyCall(@PathVariable String callId) {
        if (!Main.activeCalls.contains(callId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Call not found.");
        }

        try {
            controller.modifyCall(accountId, callId, new ApiModifyCallRequest.Builder()
                .redirectUrl(baseUrl + "/callbacks/goodbye")
                .build()
            );
            Main.activeCalls.remove(callId);

        } catch (ApiException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
