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

import io.bootique.test.junit.BQTestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Ruslan Ibragimov
 */
public class CxfJaxrsModuleDefaultIT {
    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @Before
    public void startJetty() {
        testFactory.app("-s").run();
    }

    @Test
    public void testResponse() throws IOException {
        final HttpResponse response = Request.Get("http://localhost:8080/").execute().returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertEquals("CXF REST API Module", EntityUtils.toString(response.getEntity()));
    }
}
