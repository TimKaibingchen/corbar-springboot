package com.bwts.invoice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bwts.common.exception.APIException;
import com.bwts.common.exception.ErrorCode;
import com.bwts.invoice.message.KafkaProducer;
import com.bwts.invoice.service.ThirdPartyService;

@Component
@Path("external/documents")
public class ExternalDocumentResource {
    
    private final KafkaProducer kafkaProducer;

    private final ThirdPartyService thirdPartyService;

    @Value("${access.token.enabled}") private boolean tokenEnabled;

    @Autowired
    public ExternalDocumentResource(KafkaProducer kafkaProducer, ThirdPartyService thirdPartyService) {
        this.kafkaProducer = kafkaProducer;
        this.thirdPartyService = thirdPartyService;

    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response createDocument(String message, @HeaderParam("BWTS-TOKEN") String token) {
        if (tokenEnabled) {
            if (token == null) {
                throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.NO_TOKEN, "no bwts-token in header");
            }
            if (!thirdPartyService.verifyToken(token)) {
                throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN, "invalid token");
            }
        }
        return sendKafkaMessage(message);
    }

    private Response sendKafkaMessage(String message) {
        kafkaProducer.send(message);
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
