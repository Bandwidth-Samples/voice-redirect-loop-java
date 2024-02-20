package com.bandwidth.controller;

import com.bandwidth.Main;
import com.bandwidth.sdk.model.InitiateCallback;
import com.bandwidth.sdk.model.RedirectCallback;
import com.bandwidth.sdk.api.*;
import com.bandwidth.sdk.model.bxml.*;
import com.bandwidth.sdk.model.bxml.Redirect;
import com.bandwidth.sdk.model.bxml.Response;
import com.bandwidth.sdk.model.bxml.Ring;
import com.bandwidth.sdk.model.bxml.Bxml;
import com.bandwidth.sdk.model.bxml.SpeakSentence;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
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
    public String inboundCall(@RequestBody InitiateCallback callback) throws JAXBException {

        Response response = new Response();
	JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);

        String eventType = callback.getEventType();

        logger.info(eventType);
        logger.info(callback.getCallId());

        if("initiate".equalsIgnoreCase(eventType) || "redirect".equalsIgnoreCase(eventType)) {

            Ring ring = new Ring(30d, true);

            Redirect redirect = new Redirect().builder()
                .redirectUrl("/callbacks/inbound")
                .build();

            response.withVerbs(ring, redirect);
        }

        if ("initiate".equalsIgnoreCase(eventType)) {
            Main.activeCalls.add(callback.getCallId());
        }

	logger.info(response.toBxml(jaxbContext));

        return response.toBxml(jaxbContext);

    }

    @RequestMapping("/callEnded")
    public String outboundCall(@RequestBody RedirectCallback callback) throws JAXBException {

        Response response = new Response();
	JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);

        logger.info(callback.getEventType());
        logger.info(callback.getCallId());

        if("redirect".equalsIgnoreCase(callback.getEventType())) {
            SpeakSentence ss = new SpeakSentence("Call successfully updated. Goodbye.");

            response.with(ss);

        }
        return response.toBxml(jaxbContext);
    }

}
