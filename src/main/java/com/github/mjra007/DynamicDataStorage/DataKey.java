package com.github.mjra007.DynamicDataStorage;

import java.io.Serializable;

/**
 * A Data Key holds a allowed type {@link DataKey#getAllowedType} and it is used to infer the type of an object in a {@link DynamicDataStorageMap}
 * The ID should be unique as the DynamicDataStorageMap object wont accept duplicated data keys.
 * @param <T>
 */
public class DataKey<T> implements Serializable {

    Class<T> Type;
    String ID;

    private DataKey(Class<T> clazz, String ID){
        Type = clazz;
        this.ID = ID;
    }

    private DataKey(){}

    public static <T,D extends T> DataKey<T> makeKeyFor(Class<D> allowedType, String keyName){
        return new DataKey<T>(generify(allowedType), keyName);
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> generify(Class<?> cls) {
        return (Class<T>)cls;
    }

    Class<T> getAllowedType(){
        return Type;
    }

    public String getID() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DataKey)) return false;
        DataKey other = (DataKey) o;
        return this.ID.equals(other.getID());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash +  ID.hashCode();
        return hash;
    }


}
