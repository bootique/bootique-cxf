<!--
   Licensed to ObjectStyle LLC under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ObjectStyle LLC licenses
   this file to you under the Apache License, Version 2.0 (the
   “License”); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
  -->


# bootique-cxf-core

Provides and configures core CXF components, such as Bus, Extensions, Configurer, Features, Interceptors etc. 
It allows you to configure and extend CXF via Guice dependency injection.
This module is not intended to be used by itself, rather in the conjunction with [JAX-WS Server](../bootique-cxf-jaxws-server), [JAX-WS Client](../bootique-cxf-jaxws-client) or [JAX-RS Server](../bootique-cxf-jaxrs) modules.

### Providing CXF features
A [Feature](http://cxf.apache.org/docs/features.html) in CXF is a way of adding capabilities to a Server, Client or Bus. For example, you could add the ability to log messages for each of these objects, by configuring them with a LoggingFeature.
You can provide them in your application module: 
```java
class App implements Module {
    public void config(Binder binder) {
        CxfModule.extend(binder)
                .addFeature(LoggingFeature.class)
                .addFeature(new GZIPFeature());
    }
}
```
### Providing CXF Bus interceptors

[Interceptors](http://cxf.apache.org/docs/interceptors.html) are the fundamental processing unit inside CXF. When a service is invoked, an InterceptorChain is created and invoked. Each interceptor gets a chance to do what they want with the message. This can include reading it, transforming it, processing headers, validating the message, etc. 
```java
class App implements Module {
    public void config(Binder binder) {
        CxfModule.extend(binder)
                        .contributeBusInterceptors()
                        .addInInterceptor(new LoggingInInterceptor())
                        .addOutInterceptor(GZIPOutInterceptor.class);
    }
}
``` 
Bus interceptors will be used both in clients and server endpoints. If you want to provide client-only or server-only interceptors, check [client](../bootique-cxf-jaxws-client) and [server](../bootique-cxf-jaxws-server) modules.

## Advanced configuration
### Providing extensions
CXF uses extensions mechanism to provide core internal components. You can use Guice binding to override any of them:
```java
class App implements Module {
    public void config(Binder binder) {
        binder.bind(WSDLManager.class).to(CustomWSDLManager.class);
    }
}
```

### Configuring beans
CXF provides mechanism for configuring internally created beans. Those implement `org.apache.cxf.configuration.Configurable` interface and include such entities, as `EndpointImpl`, `ServiceImpl`, `HTTPConduit` etc. You can provide your own custom configurer in a module:
```java
class App implements Module {
    public void config(Binder binder) {
        CxfModule.extend(binder)
                .addCustomConfigurer(HTTPConduit.class, CustomHTTPConduitConfigurer.class);
    }
}
```

