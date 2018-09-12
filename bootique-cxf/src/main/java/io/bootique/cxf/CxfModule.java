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

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.annotations.CxfFeature;
import io.bootique.cxf.annotations.CxfResource;
import io.bootique.cxf.annotations.CxfServlet;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
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
public class CxfModule extends ConfigModule {

    /**
     * Returns an instance of {@link CxfModuleExtender} used by downstream modules to load custom extensions of
     * services declared in the CxfModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link CxfModuleExtender} that can be used to load CXF custom extensions.
     * @since 1.0.RC1
     */
    public static CxfModuleExtender extend(Binder binder) {
        return new CxfModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        CxfModule.extend(binder).initAllExtensions();

        final TypeLiteral<MappedServlet<Servlet>> servletTypeLiteral = new TypeLiteral<MappedServlet<Servlet>>() {};
        final Key<MappedServlet<Servlet>> servletKey = Key.get(servletTypeLiteral, CxfServlet.class);
        JettyModule.extend(binder).addMappedServlet(servletKey);
        CxfModule.extend(binder).addResource(CxfDefaultService.class);
    }

    @CxfServlet
    @Singleton
    @Provides
    private MappedServlet<Servlet> createCxfServlet(CxfModuleConfig config, Application application) {
        CXFNonSpringJaxrsServlet servlet = new CXFNonSpringJaxrsServlet(application);
        return new MappedServlet<>(servlet, Collections.singleton(config.getUrlPattern()), CxfServlet.class.getName());
    }

    @Singleton
    @Provides
    private CxfModuleConfig createCxfFactory(ConfigurationFactory configFactory) {
        return configFactory.config(CxfModuleConfig.class, configPrefix);
    }

    @Singleton
    @Provides
    private Application createApplication(@CxfResource Set<Object> resources, @CxfFeature Set<Feature> features) {
        final Map<String, String> props = new HashMap<>();

        props.put("jaxrs.inInterceptors", LoggingInInterceptor.class.getName());

        return new CxfApplication(resources, features, props);
    }
}
