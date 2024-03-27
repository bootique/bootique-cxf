package io.bootique.cxf.jaxws;

import io.bootique.cxf.conf.CustomConfigurer;
import org.apache.cxf.transport.http.HttpClientHTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;


public class HttpClientHTTPConduitConfigurer implements CustomConfigurer<HttpClientHTTPConduit> {

    private final boolean followRedirects;
    private final int readTimeoutMs;
    private final int connectTimeoutMs;

    public HttpClientHTTPConduitConfigurer(boolean followRedirects, int readTimeoutMs, int connectTimeoutMs) {
        this.followRedirects = followRedirects;
        this.readTimeoutMs = readTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    @Override
    public void configure(HttpClientHTTPConduit httpConduit) {
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
