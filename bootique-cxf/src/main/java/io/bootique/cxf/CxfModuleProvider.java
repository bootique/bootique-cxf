package io.bootique.cxf;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

/**
 * TODO.
 *
 * @author TODO
 * @since 0.26
 */
public class CxfModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfModule();
    }

    @Override
    public Map<String, Type> configs() {
        // TODO: config prefix is hardcoded. Refactor away from ConfigModule, and make provider
        // generate config prefix, reusing it in metadata...
        return Collections.singletonMap("cxf", CxfFactory.class);
    }
}
