package org.openshift.webservices;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;


/**
 * Created with IntelliJ IDEA.
 * User: spousty
 * Date: 11/11/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */

@Provider
@ServerInterceptor
public class PostProcessInfoSender implements PostProcessInterceptor{
    Logger logger = Logger.getLogger(PostProcessInfoSender.class);


    @Override
    public void postProcess(ServerResponse serverResponse) {
        logger.error("before parameter grab");
        MultivaluedMap<String, Object> headers = serverResponse.getMetadata();
        logger.error("READ ME:: " + headers.get(HttpHeaderNames.CONTENT_LENGTH));

    }
}
