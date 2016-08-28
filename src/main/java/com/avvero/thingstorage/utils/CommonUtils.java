package com.avvero.thingstorage.utils;

import com.avvero.thingstorage.exception.ThingStorageException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by avvero on 28.08.2016.
 */
public class CommonUtils {

    public static Pair<Integer, Integer> getDimensions(String wxh) {
        int w;
        int h;
        try {
            String[] parts = wxh.split("x");
            w = Integer.parseInt(parts[0]);
            h = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            throw new ThingStorageException("Incorrect dimension");
        }
        return new ImmutablePair(w, h);
    }

}
