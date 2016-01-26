package com.bwts.invoice.rest;

import com.bwts.common.exception.APIException;
import com.bwts.common.kafka.producer.KafkaMessageProducer;
import com.bwts.invoice.service.ThirdPartyService;
import com.hj.cobar.bean.Cont;
import com.hj.cobar.query.ContQuery;
import com.hj.cobar.service.ContService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component
@Path("/data")
public class ExternalDocumentResource {

    private final ThirdPartyService thirdPartyService;

    @Value("${access.token.enabled}") private boolean tokenEnabled;

    private final KafkaMessageProducer messageProducer;

    private final String invoiceTopic;

    private ContService contService;

    @Autowired
    public ExternalDocumentResource(ThirdPartyService thirdPartyService,
                                    KafkaMessageProducer producer,
                                    ContService contService,
                                    @Value("${consumer.invoice.topic}") String topic) {
        this.thirdPartyService = thirdPartyService;
        this.messageProducer = producer;
        this.invoiceTopic = topic;
        this.contService = contService;
    }

    @POST
    public Response saveDBData(@QueryParam("count") int count, @QueryParam("start") int start) throws APIException {
        for (int i = 0; i < count; i++) {
            Cont cont = new Cont();
            cont.setName("gd" + i);
            Long taobaoId = new Long(start + (long)(Math.random() * count));
            cont.setTaobaoId(taobaoId);
            contService.addCont(cont);
        }
        return Response.status(Response.Status.ACCEPTED).build();
    }


    @GET
    public Response getDBData() throws APIException {
        Map map = new HashMap();
        ContQuery contQuery = new ContQuery();
        contQuery.setShardEnd(10001l);
        contQuery.setTaobaoId(100l);
        Integer db1 = contService.getCount(contQuery);
        map.put("db1", db1);

        contQuery.setShardStart(10000l);
        contQuery.setShardEnd(20001l);
        contQuery.setTaobaoId(10001l);
        Integer db2 = contService.getCount(contQuery);
        map.put("db2", db2);

        contQuery.setShardStart(20000l);
        contQuery.setShardEnd(null);
        contQuery.setTaobaoId(20002l);
        Integer db3 = contService.getCount(contQuery);
        map.put("db3", db3);
        return Response.ok(map).build();
    }


}
