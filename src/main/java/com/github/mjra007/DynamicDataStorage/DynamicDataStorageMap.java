package com.github.mjra007.DynamicDataStorage;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.util.DefaultInstantiatorStrategy;
import com.google.common.collect.ImmutableMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A DynamicDataStorageMap leverages type erasure to create a dynamic data storage
 * By using {@link DataKey<?>} the user is able to infer the types when retrieving objects that way avoiding explicit casting.
 */
public class DynamicDataStorageMap implements Serializable {

    private final Map<DataKey<?>, Object> GenericDataMap;

    public enum TransactionResponse {TRYING_TO_ADD_EXISTING_KEY, KEY_DOES_NOT_EXIST, SUCCESS, NULL_VALUE}

    ;

    public DynamicDataStorageMap() {
        GenericDataMap = new HashMap<>();
    }

    /**
     * Returns a data object in userdata in the respective type given the data key provided
     *
     * @param key {@link DataKey} the key used to reference the data
     * @param <T> the type of data to be returned {@link DataKey#getAllowedType()} ()}
     * @return an {@link Optional} that might be empty if {@link DynamicDataStorageMap#GenericDataMap} does not contain key provided
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(DataKey<T> key) {
        return GenericDataMap.containsKey(key) ? Optional.of((T) GenericDataMap.get(key)) : Optional.empty();
    }

    /**
     * Replaces a value for give key.
     *
     * @param key   key {@link DataKey} the key used to reference the data to be replaced
     * @param value the value {@link DataKey#getAllowedType()} that will substitute the previous value object
     * @param <T>   the type of data the key is holding {@link DataKey#getAllowedType()}
     * @return If {@link DynamicDataStorageMap#GenericDataMap} does not contain given key returns {@link TransactionResponse#KEY_DOES_NOT_EXIST}
     * If replace was successful returns {@link TransactionResponse#SUCCESS}
     * If key does not exist returns {@link TransactionResponse#KEY_DOES_NOT_EXIST}
     */
    public <T> TransactionResponse replace(DataKey<T> key, T value) {
        if (!GenericDataMap.containsKey(key)) {
            return TransactionResponse.KEY_DOES_NOT_EXIST;
        } else {
            GenericDataMap.put(key, value);
            return TransactionResponse.SUCCESS;
        }
    }

    /**
     * @return an Immutable copy of all the player data
     */
    public ImmutableMap<DataKey<?>, Object> copy() {
        return ImmutableMap.copyOf(this.GenericDataMap);
    }

    public <T> TransactionResponse add(DataKey<T> key, T value) {
        if (value == null) return TransactionResponse.NULL_VALUE;
        if (GenericDataMap.containsKey(key)) {
            return TransactionResponse.TRYING_TO_ADD_EXISTING_KEY;
        } else {
            GenericDataMap.put(key, value);
            return TransactionResponse.SUCCESS;
        }
    }

    public static void write(DynamicDataStorageMap dynamicDataStorageMap, Path path) throws FileNotFoundException {
        Output output = new Output(new FileOutputStream(path.toFile()));
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy());
        kryo.setRegistrationRequired(false);
        kryo.writeObject(output, dynamicDataStorageMap);
        output.close();
    }

    public static DynamicDataStorageMap read(Path path) throws FileNotFoundException {
        Input input = new Input(new FileInputStream(path.toFile()));
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy());
        kryo.setRegistrationRequired(false);
        input.close();
        return kryo.readObject(input, DynamicDataStorageMap.class);
    }

}
