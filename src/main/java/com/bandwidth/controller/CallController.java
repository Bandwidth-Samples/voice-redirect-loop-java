package com.bandwidth.controller;

import com.bandwidth.Main;

import com.bandwidth.sdk.ApiClient;
import com.bandwidth.sdk.api.CallsApi;
import com.bandwidth.sdk.model.UpdateCall;

import java.net.URI;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("calls")
public class CallController {

    Logger logger = LoggerFactory.getLogger(CallController.class);

    private final String accountId = System.getenv("BW_ACCOUNT_ID");
    private final String clientId = System.getenv("BW_CLIENT_ID");
    private final String clientSecret = System.getenv("BW_CLIENT_SECRET");
    private final String baseCallbackUrl = System.getenv("BASE_CALLBACK_URL");

    private final CallsApi callsApi = new CallsApi(new ApiClient(clientId, clientSecret, null));

    @GetMapping()
    public Set<String> getCalls() {
        return Main.activeCalls;
    }

    @DeleteMapping("/{callId}")
    public ResponseEntity<Void> deleteCall(@PathVariable("callId") String callId) {
        if (!Main.activeCalls.contains(callId)) {
            return ResponseEntity.notFound().build();
        }

        UpdateCall updateCall = new UpdateCall()
                .redirectUrl(URI.create(baseCallbackUrl + "/callbacks/inbound/voice/callEnded"));

        try {
            callsApi.updateCall(accountId, callId, updateCall);
        } catch (Exception e) {
            logger.error("Failed to update call: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        Main.activeCalls.remove(callId);
        return ResponseEntity.noContent().build();
    }
}
