package com.bandwidth.controller;

import com.bandwidth.sdk.ApiClient;
import com.bandwidth.sdk.ApiResponse;
import com.bandwidth.sdk.ApiException;
import com.bandwidth.sdk.ApiClient;
import com.bandwidth.sdk.auth.HttpBasicAuth;
import com.bandwidth.sdk.Configuration;
import com.bandwidth.sdk.model.*;
import com.bandwidth.sdk.api.CallsApi;
import com.bandwidth.sdk.model.bxml.Redirect;
import com.bandwidth.sdk.model.bxml.Bxml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import com.bandwidth.Main;

import java.net.URI;
import java.net.URISyntaxException;
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

    public final String username = System.getenv("BW_USERNAME");
    public final String password = System.getenv("BW_PASSWORD");
    public final String accountId = System.getenv("BW_ACCOUNT_ID");
    public final String baseUrl = System.getenv("BW_BASE_URL");

    public ApiClient defaultClient = Configuration.getDefaultApiClient();
    public HttpBasicAuth Basic = (HttpBasicAuth) defaultClient.getAuthentication("Basic");
    public final CallsApi api = new CallsApi(defaultClient);
    private static UpdateCall updateCallBody = new UpdateCall();
    

    @GetMapping("/activeCalls")
    public Set<String> getActiveCalls() {
        return Main.activeCalls;
    }

    @DeleteMapping("/calls/{callId}")
    public void updateCall(@PathVariable String callId) throws URISyntaxException {

        if (!Main.activeCalls.contains(callId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Call not found.");
        }

	updateCallBody.setRedirectUrl(new URI(baseUrl + "/callbacks/goodbye"));

        try {
	    Basic.setUsername(username);
	    Basic.setPassword(password);
	    api.updateCallWithHttpInfo(accountId,callId,updateCallBody);
            Main.activeCalls.remove(callId);

        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
