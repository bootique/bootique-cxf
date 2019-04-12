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

/**
 * CXF configuration.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.RC1
 */
@BQConfig("Configures the servlet that is an entry point to CXF REST API engine.")
public class CxfModuleConfig {
    private String urlPattern;

    private String welcomeText;

    public CxfModuleConfig() {
        this.urlPattern = "/*";
        this.welcomeText = "CXF REST API Module";
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    @BQConfigProperty
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    @BQConfigProperty
    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }
}