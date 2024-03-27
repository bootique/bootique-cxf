/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.cxf;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Application;
import java.util.Objects;
import java.util.Set;

/**
 * CXF configuration.
 *
 * @deprecated The users are encouraged to switch to the Jakarta-based flavor
 */
@Deprecated(since = "3.0", forRemoval = true)
@BQConfig("Configures the servlet that is an entry point to CXF REST API engine.")
public class CxfJaxrsServletFactory {

    private final Provider<Application> application;
    private final Provider<Bus> bus;

    private String urlPattern;
    private String welcomeText;

    @Inject
    public CxfJaxrsServletFactory(Provider<Application> application, Provider<Bus> bus) {
        this.application = Objects.requireNonNull(application);
        this.bus = Objects.requireNonNull(bus);
    }

    public MappedServlet<CXFNonSpringJaxrsServlet> createServlet() {
        CXFNonSpringJaxrsServlet servlet = new CXFNonSpringJaxrsServlet(application.get());
        servlet.setBus(bus.get());

        String urlPattern = this.urlPattern != null ? this.urlPattern : "/*";
        return new MappedServlet<>(servlet, Set.of(urlPattern));
    }

    public CxfDefaultService createDefaultService() {
        String welcomeText = this.welcomeText != null ? this.welcomeText : "CXF REST API Module";
        return new CxfDefaultService(welcomeText);
    }

    @BQConfigProperty
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @BQConfigProperty
    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }
}
