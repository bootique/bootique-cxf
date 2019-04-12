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

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.jetty.JettyModuleProvider;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Standard {@link BQModuleProvider} for {@link CxfJaxrsModule}.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.RC1
 */
public class CxfJaxrsModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfJaxrsModule();
    }

    @Override
    public Map<String, Type> configs() {
        // TODO: config prefix is hardcoded. Refactor away from ConfigModule, and make provider
        // generate config prefix, reusing it in metadata...
        return Collections.singletonMap("cxf", CxfJaxrsModuleConfig.class);
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new JettyModuleProvider(),
                new CxfModuleProvider()
        );
    }
}
