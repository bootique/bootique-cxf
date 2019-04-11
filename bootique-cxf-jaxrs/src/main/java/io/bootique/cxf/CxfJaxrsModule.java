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

import com.google.inject.*;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.annotations.CxfFeature;
import io.bootique.cxf.annotations.CxfResource;
import io.bootique.cxf.annotations.CxfServlet;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * CXF module.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.RC1
 */
public class CxfJaxrsModule extends ConfigModule {

    /**
     * Returns an instance of {@link CxfJaxrsModuleExtender} used by downstream modules to load custom extensions of
     * services declared in the CxfJaxrsModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link CxfJaxrsModuleExtender} that can be used to load CXF custom extensions.
     * @since 1.0.RC1
     */
    public static CxfJaxrsModuleExtender extend(Binder binder) {
        return new CxfJaxrsModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        CxfJaxrsModule.extend(binder).initAllExtensions();

        final TypeLiteral<MappedServlet<Servlet>> servletTypeLiteral = new TypeLiteral<MappedServlet<Servlet>>() {};
        final Key<MappedServlet<Servlet>> servletKey = Key.get(servletTypeLiteral, CxfServlet.class);
        JettyModule.extend(binder).addMappedServlet(servletKey);
        CxfJaxrsModule.extend(binder).addResource(CxfDefaultService.class);
    }

    @CxfServlet
    @Singleton
    @Provides
    private MappedServlet<Servlet> createCxfServlet(CxfJaxrsModuleConfig config, Application application, Bus bus) {

        CXFNonSpringJaxrsServlet servlet = new CXFNonSpringJaxrsServlet(application);
        servlet.setBus(bus);

        return new MappedServlet<>(servlet, Collections.singleton(config.getUrlPattern()), CxfServlet.class.getName());
    }

    @Singleton
    @Provides
    private CxfJaxrsModuleConfig createCxfFactory(ConfigurationFactory configFactory) {
        return configFactory.config(CxfJaxrsModuleConfig.class, configPrefix);
    }

    @Singleton
    @Provides
    private Application createApplication(@CxfResource Set<Object> resources, @CxfFeature Set<Feature> features) {
        final Map<String, String> props = new HashMap<>();

        // TODO deliver interceptors in module extender
        props.put("jaxrs.inInterceptors", LoggingInInterceptor.class.getName());

        return new CxfApplication(resources, features, props);
    }
}
