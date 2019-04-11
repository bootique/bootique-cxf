package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import io.bootique.ModuleExtender;

public class CxfJaxwsServerModuleExtender extends ModuleExtender<CxfJaxwsServerModuleExtender> {


    public CxfJaxwsServerModuleExtender(Binder binder) {
        super(binder);
    }



    @Override
    public CxfJaxwsServerModuleExtender initAllExtensions() {
        return null;
    }


}
