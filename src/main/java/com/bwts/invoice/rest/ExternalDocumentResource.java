package com.bwts.invoice.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bwts.invoice.message.KafkaProducer;

@Component
@Path("external/documents")
public class ExternalDocumentResource {
    
    private final KafkaProducer kafkaProducer;
    
    @Autowired
    public ExternalDocumentResource(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }
    
    @POST
    public void createDocument(String message) {
        kafkaProducer.send(message);
    }
}
