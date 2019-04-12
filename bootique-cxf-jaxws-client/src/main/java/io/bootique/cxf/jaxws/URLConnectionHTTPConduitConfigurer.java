package io.bootique.cxf.jaxws;

import io.bootique.cxf.conf.CustomConfigurer;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class URLConnectionHTTPConduitConfigurer implements CustomConfigurer<URLConnectionHTTPConduit> {

    private final boolean followRedirects;
    private final int readTimeoutMs;
    private final int connectTimeoutMs;

    public URLConnectionHTTPConduitConfigurer(boolean followRedirects, int readTimeoutMs, int connectTimeoutMs) {
        this.followRedirects = followRedirects;
        this.readTimeoutMs = readTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    @Override
    public void configure(URLConnectionHTTPConduit httpConduit) {
        HTTPClientPolicy clientPolicy = httpConduit.getClient();

        if (clientPolicy == null) {
            clientPolicy = new HTTPClientPolicy();
            httpConduit.setClient(clientPolicy);
        }

        clientPolicy.setAutoRedirect(followRedirects);
        clientPolicy.setReceiveTimeout(readTimeoutMs);
        clientPolicy.setConnectionTimeout(connectTimeoutMs);

    }
}
