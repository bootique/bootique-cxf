package io.bootique.cxf.jaxws;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@BQConfig("Configures JAX-WS client entities, including named urls")
public class CxfJaxwsClientConfiguration {

    boolean followRedirects;
    int readTimeoutMs;
    int connectTimeoutMs;

    Map<String, URL> urls;

    public CxfJaxwsClientConfiguration() {
        this.followRedirects = false;
        this.readTimeoutMs = 60 * 1000;
        this.connectTimeoutMs = 30 * 1000;
        this.urls = new HashMap<>();

    }

    @BQConfigProperty("Sets whether the client should autromatically follow redirects. The default is 'true'.")
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    @BQConfigProperty("Sets the read timeout. The default is 60000 (60 sec). 0 means no timeout.")
    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    @BQConfigProperty("Sets the connect timeout. The default is 30000 (30 sec). (0) means no timeout.")
    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }


    /**
     * Sets a map of named urls. This allows to define remote endpoints completely via configuration.
     *
     * @param urls a map of named target factories.
     */
    @BQConfigProperty
    public void setUrls(Map<String, URL> urls) {
        this.urls = urls;
    }

}
