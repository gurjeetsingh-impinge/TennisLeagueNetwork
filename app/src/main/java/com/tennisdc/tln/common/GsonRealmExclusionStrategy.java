package com.tennisdc.tln.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import io.realm.RealmObject;

/**
 * Created  on 2015-03-12.
 */
public class GsonRealmExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass().equals(RealmObject.class);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

}
