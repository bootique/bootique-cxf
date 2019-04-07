package io.bootique.cxf.jaxws;

import javax.jws.WebService;

@WebService
public interface HelloWorld {
    String sayHi(String text);
}
