package io.bootique.cxf;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @deprecated The users are encouraged to switch to the Jakarta-based flavor
 */
@Deprecated(since = "3.0", forRemoval = true)
final class MapUtils {

    /**
     * Merges maps to a map of lists. Order of input matters.
     */
    @SafeVarargs
    static  <K,V> Map<K, List<V>> mergeMaps(Map<K, V>... maps) {
        return Stream.of(maps)
                .flatMap(m -> m.entrySet().stream())
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.toList()
                                )
                        )
                );
    }
}
