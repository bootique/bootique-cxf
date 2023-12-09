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

import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * CXF configuration.
 */
@BQConfig("Configures the servlet that is an entry point to CXF REST API engine.")
public class CxfJaxrsServletFactory {

    private String urlPattern;
    private String welcomeText;

    public MappedServlet<CXFNonSpringJaxrsServlet> createServlet(Application application, Bus bus) {
        CXFNonSpringJaxrsServlet servlet = new CXFNonSpringJaxrsServlet(application);
        servlet.setBus(bus);

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