package com.bandwidth.controller;

import com.bandwidth.Main;
import com.bandwidth.sdk.model.DisconnectCallback;
import com.bandwidth.sdk.model.InitiateCallback;
import com.bandwidth.sdk.model.RedirectCallback;
import com.bandwidth.sdk.model.bxml.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("callbacks")
public class CallbacksController {

    Logger logger = LoggerFactory.getLogger(CallbacksController.class);

    @PostMapping(value = "/inbound/voice", produces = "application/xml")
    public ResponseEntity<String> inboundCallback(@RequestBody InitiateCallback callback) throws JAXBException {
        String eventType = callback.getEventType();
        if (!"initiate".equals(eventType) && !"redirect".equals(eventType)) {
            return ResponseEntity.badRequest().build();
        }

        if ("initiate".equals(eventType)) {
            Main.activeCalls.add(callback.getCallId());
        }

        Response response = new Response();
        response.withVerbs(
                new SpeakSentence("Redirecting call, please wait."),
                Ring.builder().duration(30d).build(),
                Redirect.builder().redirectUrl("/callbacks/inbound/voice").build()
        );

        return ResponseEntity.ok(response.toBxml(JAXBContext.newInstance(Response.class)));
    }

    @PostMapping(value = "/inbound/voice/callEnded", produces = "application/xml")
    public ResponseEntity<String> callEndedCallback(@RequestBody RedirectCallback callback) throws JAXBException {
        if (!"redirect".equals(callback.getEventType())) {
            return ResponseEntity.badRequest().build();
        }

        Response response = new Response();
        response.withVerbs(
                new SpeakSentence("The call has been ended. Goodbye."),
                new Hangup()
        );

        return ResponseEntity.ok(response.toBxml(JAXBContext.newInstance(Response.class)));
    }

    @PostMapping("/inbound/voice/disconnect")
    public ResponseEntity<Void> disconnectCallback(@RequestBody DisconnectCallback callback) {
        if (!"disconnect".equals(callback.getEventType())) {
            return ResponseEntity.badRequest().build();
        }

        Main.activeCalls.remove(callback.getCallId());
        return ResponseEntity.noContent().build();
    }

}
