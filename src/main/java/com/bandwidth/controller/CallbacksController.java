package com.bandwidth.controller;

import com.bandwidth.Main;
import com.bandwidth.model.VoiceCallback;
import com.bandwidth.voice.bxml.verbs.Redirect;
import com.bandwidth.voice.bxml.verbs.Response;
import com.bandwidth.voice.bxml.verbs.Ring;
import com.bandwidth.voice.bxml.verbs.SpeakSentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("callbacks")
public class CallbacksController {

    Logger logger = LoggerFactory.getLogger(CallbacksController.class);

    @RequestMapping("/inbound")
    public String inboundCall(@RequestBody VoiceCallback callback) {

        Response response = new Response();

        String eventType = callback.getEventType();

        logger.info(eventType);
        logger.info(callback.getCallId());

        if("initiate".equalsIgnoreCase(eventType) || "redirect".equalsIgnoreCase(eventType)) {

            Ring ring = Ring.builder().duration(30.0).build();

            Redirect redirect = Redirect.builder()
                .redirectUrl("/callbacks/inbound")
                .build();

            response.addAll(ring, redirect);
        }

        if ("initiate".equalsIgnoreCase(eventType)) {
            Main.activeCalls.add(callback.getCallId());
        }

        return response.toBXML();

    }

    @RequestMapping("/goodbye")
    public String outboundCall(@RequestBody VoiceCallback callback) {

        Response response = new Response();

        logger.info(callback.getEventType());
        logger.info(callback.getCallId());

        if("redirect".equalsIgnoreCase(callback.getEventType())) {
            SpeakSentence ss = SpeakSentence.builder()
                .text("Call successfully updated. Goodbye.")
                .build();

            response.addAll(ss);
        }
        return response.toBXML();
    }

}
