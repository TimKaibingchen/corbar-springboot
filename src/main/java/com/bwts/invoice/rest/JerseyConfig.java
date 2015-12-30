package com.bwts.invoice.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.bwts.common.exception.APIExceptionMapper;
import com.bwts.common.exception.GenericExceptionMapper;
import com.bwts.common.exception.NotFoundExceptionMapper;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ExternalDocumentResource.class);
        register(APIExceptionMapper.class);
        register(GenericExceptionMapper.class);
        register(NotFoundExceptionMapper.class);
    }
}
