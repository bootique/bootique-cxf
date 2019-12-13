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

package io.bootique.cxf.interceptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * @since 2.0
 */
public class CxfInterceptorAnnotationHolder {

    private final List<Class<? extends Annotation>> annotationTypes;

    public CxfInterceptorAnnotationHolder(Class<? extends Annotation> in,
                                          Class<? extends Annotation> inFault,
                                          Class<? extends Annotation> out,
                                          Class<? extends Annotation> outFault) {
        this.annotationTypes = Arrays.asList(in, inFault, out, outFault);
    }

    public Class<? extends Annotation> getIn() {
        return annotationTypes.get(0);
    }

    public Class<? extends Annotation> getInFault() {
        return annotationTypes.get(1);
    }

    public Class<? extends Annotation> getOut() {
        return annotationTypes.get(2);
    }

    public Class<? extends Annotation> getOutFault() {
        return annotationTypes.get(3);
    }
}
