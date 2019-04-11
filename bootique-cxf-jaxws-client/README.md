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


# bootique-cxf-jaxws-client

Provides [Apache CXF](https://cxf.apache.org/) JAX-WS client integration with [Bootique](http://bootique.io).
Allows to configure multiple client runtime parameters, as well as define server URL endpoints.

## Quick start

Add the module to your Bootique app:

```xml
<dependency>
    <groupId>io.bootique.cxf</groupId>
    <artifactId>bootique-cxf-jaxwsclient</artifactId>
</dependency>
```

Now, you can create the client proxies from anywhere in the code:
```java
JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
proxyFactoryBean.setAddress("http://example.com/ws/HelloWorld");
HelloWorld helloWorldClient = proxyFactoryBean.create(HelloWorld.class);
        
String responseFromClient = helloWorldClient.sayHi("Simple Client");
```  

## Configuring Connection Parameters

You can specify a number of runtime parameters for your HTTP clients via
the app ```.yml``` (or any other Bootique configuration mechanism):

```yml
cxfjaxwsclient:
  followRedirects: true
  readTimeoutMs: 2000
  connectTimeoutMs: 2000
```

## Mapping URL Targets

In the example above hardcoded the endpoint URL in Java. Instead you
can map multiple URLs in the ```.yml```, assigning each URL a symbolic
name:

```yml
cxfjaxwsclient:
  urls:
    google: http://google.com/
    bootique: http://bootique.com/ws/hello
    bootique-wsdl: http://bootique.com/ws/hello?wsdl
```
Now you can inject `NamedURLs` map and use it as a target for client:
by name:
```java
@Inject
@NamedURLs
private Map<String, URL> urls;

public void doSomething() {

    JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
    proxyFactoryBean.setAddress(urls.get("bootique").toString());
    HelloWorld helloWorldClient = proxyFactoryBean.create(HelloWorld.class);

}
```
You can also use it in the WSDL-generated clients:
```java
@Inject
@NamedURLs
private Map<String, URL> urls;

public void doSomething() {
    
    CustomerServiceService service = new CustomerServiceService(urls.get("bootique-wsdl"));
    CustomerService customerService = service.getCustomerServicePort();
    ...
}
```
This allows to manage your URLs via configuration.
E.g. you might use a different URL between test and production environments
without changing the code.


## Extending client module
### Providing client CXF interceptors
If you want to provide client-only interceptors, you can extend server module:
```java
class App implements Module {
    public void config(Binder binder) {
        CxfJaxwsClientModule.extend(binder)
                            .contributeClientInterceptors()
                            .addInInterceptor(new LoggingInInterceptor())
                            .addOutInterceptor(GZIPOutInterceptor.class);
    }
}
```
For example, if you want to add the GZIP compression, you can contribute the following interceptors:
```java
class App implements Module {
    public void config(Binder binder) {
        CxfJaxwsClientModule.extend(binder)
                            .contributeClientInterceptors()
                            .addInInterceptor(GZIPInInterceptor.class)
                            .addOutInterceptor(GZIPOutInterceptor.class);
    }
}
```

Those will only be applied to all client instances. If you also have JAX-WS server module, this won't be affected.

## Advanced configuration
Core CXF entities, such as Bus, Extensions, Features, Configurer etc. are also configured provided by Guice. If you want to extend and expand the core CXF capabilities, please see [bootique-cxf-core](../bootique-cxf-core) module.

### Advanced HTTP configuration
CXF provides a flexible framework of [configuring an HTTP transport](http://cxf.apache.org/docs/client-http-transport-including-ssl-support.html). 
To use that, you can provide a custom `URLConnectionHTTPConduit` configurer. For example, here's how you can set up an HTTPS support, as shown in [this](https://github.com/apache/cxf/blob/bc1a22503447649e7991f4b8f3368a90deb42a7d/distribution/src/main/release/samples/wsdl_first_https/src/main/java/demo/hw_https/client/ClientNonSpring.java) example :

```java
class SSLConfigurer implements CustomConfigurer<URLConnectionHTTPConduit> {
    
    public void configure(URLConnectionHTTPConduit conduit) {
        setupTLS(conduit);
    }
    
    private void setupTLS(URLConnectionHTTPConduit conduit) {
        String keyStoreLoc = "src/main/config/clientKeystore.jks";

        TLSClientParameters tlsCP = new TLSClientParameters();
        String keyPassword = "ckpass";
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keyStoreLoc), "cspass".toCharArray());
        KeyManager[] myKeyManagers = getKeyManagers(keyStore, keyPassword);
        tlsCP.setKeyManagers(myKeyManagers);


        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(keyStoreLoc), "cspass".toCharArray());
        TrustManager[] myTrustStoreKeyManagers = getTrustManagers(trustStore);
        tlsCP.setTrustManagers(myTrustStoreKeyManagers);
        tlsCP.setDisableCNCheck(true);
        conduit.setTlsClientParameters(tlsCP);
    }

    private TrustManager[] getTrustManagers(KeyStore trustStore)
        throws NoSuchAlgorithmException, KeyStoreException {
        String alg = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory fac = TrustManagerFactory.getInstance(alg);
        fac.init(trustStore);
        return fac.getTrustManagers();
    }

    private KeyManager[] getKeyManagers(KeyStore keyStore, String keyPassword)
        throws GeneralSecurityException, IOException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        char[] keyPass = keyPassword != null
                     ? keyPassword.toCharArray()
                     : null;
        KeyManagerFactory fac = KeyManagerFactory.getInstance(alg);
        fac.init(keyStore, keyPass);
        return fac.getKeyManagers();
    }    
}
```

After that you can provide this configurer in the application module:
```java
class App implements Module {
    public void config(Binder binder) {
        CxfModule.extend(binder).addCustomConfigurer(URLConnectionHTTPConduit.class, SSLConfigurer.class);
    }
}
```