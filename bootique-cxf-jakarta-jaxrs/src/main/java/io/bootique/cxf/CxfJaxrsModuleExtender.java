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

import io.bootique.ModuleExtender;
import io.bootique.cxf.annotations.CxfFeature;
import io.bootique.cxf.annotations.CxfResource;
import io.bootique.di.Binder;
import io.bootique.di.Key;
import io.bootique.di.SetBuilder;
import org.apache.cxf.feature.Feature;

/**
 * Init all {@link SetBuilder}s on module loading.
 */
public class CxfJaxrsModuleExtender extends ModuleExtender<CxfJaxrsModuleExtender> {

    private SetBuilder<Feature> cxfFeatures;
    private SetBuilder<Object> resources;

    CxfJaxrsModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public CxfJaxrsModuleExtender initAllExtensions() {
        contributeCxfFeatures();
        contributeResources();
        return this;
    }

    public CxfJaxrsModuleExtender addResource(Object resource) {
        contributeResources().addInstance(resource);
        return this;
    }

    public CxfJaxrsModuleExtender addResource(Class<?> resource) {
        contributeResources().add(resource);
        return this;
    }


    public <T extends Feature> CxfJaxrsModuleExtender addFeature(Class<T> feature) {
        contributeCxfFeatures().add(feature);
        return this;
    }

    public <T extends Feature> CxfJaxrsModuleExtender addFeature(T feature) {
        contributeCxfFeatures().addInstance(feature);
        return this;
    }

    protected SetBuilder<Object> contributeResources() {
        if (resources == null) {
            resources = newSet(Key.get(Object.class, CxfResource.class));
        }
        return resources;
    }

    protected SetBuilder<Feature> contributeCxfFeatures() {
        if (cxfFeatures == null) {
            cxfFeatures = newSet(Key.get(Feature.class, CxfFeature.class));
        }

        return cxfFeatures;
    }
}
