package com.bwts.invoice.rest;

import com.bwts.common.exception.APIException;
import com.bwts.common.exception.ErrorCode;
import com.bwts.common.invoice.InvoiceMessage;
import com.bwts.common.message.KafkaMessageProducer;
import com.bwts.common.security.DefaultUsers;
import com.bwts.common.security.UserContext;
import com.bwts.common.security.UserContextHolder;
import com.bwts.invoice.service.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("external/documents")
public class ExternalDocumentResource {

    private final ThirdPartyService thirdPartyService;

    @Value("${access.token.enabled}") private boolean tokenEnabled;

    private final KafkaMessageProducer messageProducer;

    private final String invoiceTopic;

    @Autowired
    public ExternalDocumentResource(ThirdPartyService thirdPartyService,
            KafkaMessageProducer producer,
            @Value("${consumer.invoice.topic}") String topic) {
        this.thirdPartyService = thirdPartyService;
        this.messageProducer = producer;
        this.invoiceTopic = topic;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response createDocument(String message, @HeaderParam("BWTS-TOKEN") String token) throws APIException {
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
        UserContextHolder.setContext(new UserContext(DefaultUsers.SYSTEM));
        InvoiceMessage invoiceMessage = new InvoiceMessage.InvoiceMessgeBuilder().withInvoiceData(message).build();
        messageProducer.send(invoiceTopic,invoiceMessage);

        return Response.status(Response.Status.ACCEPTED).build();
    }
}
