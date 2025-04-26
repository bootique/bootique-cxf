package io.bootique.cxf.jaxws;

import jakarta.jws.WebService;

@WebService
public class HelloWorldImpl implements HelloWorld{
    @Override
    public String sayHi(String text) {
        return "Hello " + text;
    }
}
