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


# bootique-cxf-jaxws-server

Provides [Apache CXF](https://cxf.apache.org/) JAX-WS server integration with [Bootique](http://bootique.io).

## Quick configuration

Add the module to your Bootique app:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>3.0.M1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
...
<dependency>
    <groupId>io.bootique.cxf</groupId>
    <artifactId>bootique-cxf-jaxws-server</artifactId>
</dependency>
```

By default JAX-WS servlet will serve all requests. 
If you want to change the servlet path, you can configure it in your app ```.yml``` (or via any other Bootique-compatible mechanism). E.g.:
```yml
cxfjaxwsserver:
  urlPattern: "/ws/*" 
```   

## Configuring endpoints
After configuring modules, you can publish JAX-WS endpoints from anywhere in the code:
```java
Endpoint.publish("/test", new HelloWorldImpl())
``` 

If you want your endpoints to be configured and provided during application bootstrap, you can extent the server module in your application module:
```java
class App implements Module {
    public void config(Binder binder) {
        CxfJaxwsServerModule.extend(binder).addEndpoint(() -> Endpoint.publish("/test", new HelloWorldImpl()));
    }
}
``` 
Alternatively, you can provide a factory methods:
```java
class App implements Module {
    @ProvidesIntoSet
    @Singleton
    public Endpoint provideEndpoint(HelloWorld helloWorldService) {
        return  Endpoint.publish("/test", helloWorldService);
    }
}
``` 

Note, that Bootique is not publishing endpoinds automatically, so you have to control this manually.

CXF has many ways to create and publish endpoints. You can use any of them, if you want more control:
```java
class App implements Module {
    @ProvidesIntoSet
    @Singleton
    public Endpoint provideEndpoint(HelloWorld helloWorldService) {
        EndpointImpl endpoint = new EndpointImpl(helloWorldService);
        endpoint.getFeatures().add(new LoggingFeature());
        endpoint.getOutInterceptors().add(new GZIPOutInterceptor());
        endpoint.publish("/test");
        return endpoint;
    }
}
``` 

##### Providing service implementors
Most probably you would want to use DI to provide injections in service implementor instances. 
The easiest way to achieve that, is to use Guice factory methods, as shown in the examples above.
If you don't want or cannot use factory methods - worry not. All ```@Inject``` annotated members and methods will be resolved in runtime during endpoint creations.

```java
    @WebService
    public static class HelloWorldWithInjection implements HelloWorld {
        @Inject
        private Injectee injectee; // this will be resolved in runtime
        
        @Resource
        private WebServiceContext context; // the jax-ws resources will also be injected
        ...
    }
    ...
    
    // still works like charm
    Endpoint.publish("/test", new HelloWorldWithInjection());
    
```

## Extending server module
### Providing server CXF interceptors
If you want to provide server-only interceptors, you can extend server module:
```java
class App implements Module {
    public void config(Binder binder) {
        CxfJaxwsServerModule.extend(binder)
                            .contributeServerInterceptors()
                            .addInInterceptor(new LoggingInInterceptor())
                            .addOutInterceptor(GZIPOutInterceptor.class);
    }
}
```

Those will only be applied to all server endpoints. If you also have JAX-WS client module, this won't be affected.

## Advanced configuration
Core CXF entities, such as Bus, Extensions, Features, Configurer etc. are also configured provided by Guice. If you want to extend and expand the core CXF capabilities, please see [bootique-cxf-core](../bootique-cxf-core) module 
