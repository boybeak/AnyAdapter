package com.github.boybeak.adapter;

import android.util.SparseArray;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AnyAdapterTest {
    @Test
    public void testMap() {
        SparseArray<Class<?>> sMap = new SparseArray<>();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            sMap.put(i, String.class);
        }
        System.out.println(System.currentTimeMillis() - start1);

        Map<Integer, Class<?>> hMap = new HashMap<>();
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            hMap.put(i, String.class);
        }
        System.out.println(System.currentTimeMillis() - start2);
        System.out.println();

        long start3 = System.currentTimeMillis();
        for (int i = 0; i < sMap.size(); i++) {
            sMap.get(i);
        }
        System.out.println(System.currentTimeMillis() - start3);

        long start4 = System.currentTimeMillis();
        for (int i = 0; i < hMap.size(); i++) {
            hMap.get(i);
        }
        System.out.println(System.currentTimeMillis() - start4);

    }
}
