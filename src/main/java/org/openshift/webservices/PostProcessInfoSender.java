package org.openshift.webservices;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
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


    @Override
    public void postProcess(ServerResponse serverResponse) {
        MultivaluedMap<String, Object> headers = serverResponse.getMetadata();
        System.out.println("READ ME:: " + headers.get(HttpHeaderNames.CONTENT_LENGTH) );
    }
}
