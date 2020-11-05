package com.github.boybeak.adapter;

import android.util.SparseArray;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class AnyAdapterTest {
    @Test
    public void testMapSpeed() {
        SparseArray<Class<?>> sMap = new SparseArray<>();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            sMap.put(i, String.class);
        }
        long d1 = System.currentTimeMillis() - start1;
        System.out.println(System.currentTimeMillis() - start1);

        Map<Integer, Class<?>> hMap = new HashMap<>();
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            hMap.put(i, String.class);
        }
        long d2 = System.currentTimeMillis() - start2;
        System.out.println(d2);
        System.out.println();

        Assert.assertTrue(d1 < d2);

        long start3 = System.currentTimeMillis();
        for (int i = 0; i < sMap.size(); i++) {
            sMap.get(i);
        }
        long d3 = System.currentTimeMillis() - start3;
        System.out.println(d3);

        long start4 = System.currentTimeMillis();
        for (int i = 0; i < hMap.size(); i++) {
            hMap.get(i);
        }
        long d4 = System.currentTimeMillis() - start4;
        System.out.println(d4);
        Assert.assertTrue(d3 < d4);
    }

    @Test
    public void testGetItemCount() {
        AnyAdapter adapter = Mockito.mock(AnyAdapter.class);
        Mockito.when(adapter.getItemCount()).thenReturn(0);
    }

}
