package com.bwts.invoice.rest;

import com.bwts.common.exception.APIException;
import com.bwts.common.kafka.message.InvoiceMessage;
import com.bwts.common.kafka.producer.KafkaMessageProducer;
import com.bwts.common.security.DefaultUsers;
import com.bwts.common.security.UserContext;
import com.bwts.common.security.UserContextHolder;
import com.bwts.invoice.exception.ErrorCodes;
import com.bwts.invoice.service.ThirdPartyService;
import com.bwts.invoice.service.TokenHelper;
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
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response createDocument(String message, @HeaderParam("BWTS-TOKEN") String token) throws APIException {
        String thirdPartyCode = null;
        if (tokenEnabled) {
            if (token == null) {
                throw new APIException(HttpStatus.FORBIDDEN, ErrorCodes.NO_TOKEN);
            }
            String[] tokenValues = TokenHelper.decodeToken(token);
            if (!thirdPartyService.verifyToken(tokenValues)) {
                throw new APIException(HttpStatus.FORBIDDEN, ErrorCodes.INVALID_TOKEN);
            }
            thirdPartyCode = tokenValues[0];
            return sendKafkaMessage(thirdPartyCode, message);
        }
        return sendKafkaMessage(thirdPartyCode, message);
    }

    private Response sendKafkaMessage(String thirdPartyCode, String message) {
        UserContextHolder.setContext(new UserContext(DefaultUsers.SYSTEM));
        InvoiceMessage invoiceMessage =
                new InvoiceMessage.InvoiceMessgeBuilder().withInvoiceData(message).withThirdPartyCode(thirdPartyCode)
                        .build();
        messageProducer.send(invoiceTopic, invoiceMessage);

        return Response.status(Response.Status.ACCEPTED).build();
    }
}
