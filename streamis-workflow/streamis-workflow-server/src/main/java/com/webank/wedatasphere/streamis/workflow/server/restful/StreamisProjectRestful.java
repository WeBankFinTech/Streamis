package com.webank.wedatasphere.streamis.workflow.server.restful;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * this is the restful class for streamis project
 */

@Component
@Path("/dss/framework/release")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StreamisProjectRestful {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectRestful.class);









}
