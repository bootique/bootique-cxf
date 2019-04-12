package io.bootique.cxf;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapUtilsTest {

    @Test
    public void mergeMapsTest() {

        Map<Integer, Integer> map1 = new HashMap<>();
        map1.put(1, 1);
        map1.put(3, 2);


        Map<Integer, Integer> map2 = new HashMap<>();
        map2.put(1, 3);
        map2.put(3, 1);
        map2.put(2, 1);


        Map<Integer, List<Integer>> mergedMap = MapUtils.mergeMaps(map1, map2);

        Assert.assertArrayEquals(new Integer[]{1,3}, mergedMap.get(1).toArray());
        Assert.assertArrayEquals(new Integer[]{1}, mergedMap.get(2).toArray());
        Assert.assertArrayEquals(new Integer[]{2,1}, mergedMap.get(3).toArray());

    }
}