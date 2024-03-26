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

import io.bootique.BQModule;
import io.bootique.ModuleCrate;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.annotations.CxfFeature;
import io.bootique.cxf.annotations.CxfResource;
import io.bootique.di.Binder;
import io.bootique.di.Key;
import io.bootique.di.Provides;
import io.bootique.di.TypeLiteral;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import jakarta.ws.rs.core.Application;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

public class CxfJaxrsModule implements BQModule {

    private static final String CONFIG_PREFIX = "cxfjaxrs";

    /**
     * Returns an instance of {@link CxfJaxrsModuleExtender} used by downstream modules to load custom extensions of
     * services declared in the CxfJaxrsModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link CxfJaxrsModuleExtender} that can be used to load CXF custom extensions.
     */
    public static CxfJaxrsModuleExtender extend(Binder binder) {
        return new CxfJaxrsModuleExtender(binder);
    }

    @Override
    public ModuleCrate crate() {
        return ModuleCrate.of(this)
                .description("Integrates Apache CXF JAX-RS engine")
                .config(CONFIG_PREFIX, CxfJaxrsServletFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {
        CxfJaxrsModule.extend(binder).initAllExtensions();

        TypeLiteral<MappedServlet<CXFNonSpringJaxrsServlet>> servletTypeLiteral = new TypeLiteral<>() {
        };
        JettyModule.extend(binder).addMappedServlet(Key.get(servletTypeLiteral));

        // TODO: we probably should not have a default endpoint in the container.
        CxfJaxrsModule.extend(binder).addResource(CxfDefaultService.class);
    }

    @Singleton
    @Provides
    CxfDefaultService provideDefaultService(ConfigurationFactory configFactory) {
        return configFactory.config(CxfJaxrsServletFactory.class, CONFIG_PREFIX).createDefaultService();
    }

    @Singleton
    @Provides
    MappedServlet<CXFNonSpringJaxrsServlet> createCxfServlet(ConfigurationFactory configFactory) {
        return configFactory.config(CxfJaxrsServletFactory.class, CONFIG_PREFIX).createServlet();
    }

    @Singleton
    @Provides
    Application createApplication(@CxfResource Set<Object> resources, @CxfFeature Set<Feature> features) {
        Map<String, String> props = Map.of("jaxrs.inInterceptors", LoggingInInterceptor.class.getName());
        return new CxfApplication(resources, features, props);
    }
}
