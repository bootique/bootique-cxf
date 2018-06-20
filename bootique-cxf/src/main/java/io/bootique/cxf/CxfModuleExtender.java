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
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;
import io.bootique.cxf.annotations.CxfFeature;
import io.bootique.cxf.annotations.CxfResource;
import io.bootique.cxf.annotations.CxfServlet;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.feature.Feature;

/**
 * Init all {@link Multibinder}s on module loading.
 *
 * @author Ruslan Ibragimov
 * @since 0.26
 */
public class CxfModuleExtender extends ModuleExtender<CxfModuleExtender> {

    private Multibinder<Feature> cxfFeatures;
    private Multibinder<Object> resources;
    private Multibinder<MappedServlet> servlets;

    CxfModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public CxfModuleExtender initAllExtensions() {
        contributeCxfFeatures();
        contributeResources();
        contributeMappedServlet();

        return this;
    }

    public CxfModuleExtender addResource(Object resource) {
        contributeResources().addBinding().toInstance(resource);
        return this;
    }

    public CxfModuleExtender addResource(Class<?> resource) {
        contributeResources().addBinding().to(resource);
        return this;
    }


    public <T extends Feature> CxfModuleExtender addFeature(Class<T> feature) {
        contributeCxfFeatures().addBinding().to(feature);
        return this;
    }

    public <T extends Feature> CxfModuleExtender addFeature(T feature) {
        contributeCxfFeatures().addBinding().toInstance(feature);
        return this;
    }

    protected Multibinder<Object> contributeResources() {
        if (resources == null) {
            resources = newSet(Key.get(Object.class, CxfResource.class));
        }
        return resources;
    }

    protected Multibinder<Feature> contributeCxfFeatures() {
        if (cxfFeatures == null) {
            cxfFeatures = newSet(Key.get(Feature.class, CxfFeature.class));
        }

        return cxfFeatures;
    }

    protected Multibinder<MappedServlet> contributeMappedServlet() {
        if (servlets == null) {
            servlets = newSet(Key.get(MappedServlet.class, CxfServlet.class));
        }

        return servlets;
    }
}
