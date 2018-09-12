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

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.feature.Feature;

import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Defines the components of a JAX-RS application and supplies additional
 * meta-data. A JAX-RS application or implementation supplies a concrete
 * subclass of this abstract class.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.RC1
 */
public class CxfApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> classes = new HashSet<>();
    private final Map<String, Object> props = new HashMap<>();

    public CxfApplication(
            Set<Object> resources,
            Set<? extends Feature> features,
            Map<String, String> props
    ) {
        singletons.addAll(resources);
        singletons.addAll(features);

        classes.add(JacksonJsonProvider.class);
        classes.add(JacksonJaxbJsonProvider.class);

        this.props.putAll(props);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    @Override
    public Map<String, Object> getProperties() {
        return props;
    }
}
