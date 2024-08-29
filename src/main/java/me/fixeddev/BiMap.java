package me.fixeddev;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.AbstractSet;
import java.util.HashSet;

public class BiMap<K, V> extends AbstractMap<K, V> {
    private final Map<K, V> keyToValue = new HashMap<>();
    private final Map<V, K> valueToKey = new HashMap<>();

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Neither key nor value can be null");
        }

        // Eliminar las entradas existentes en ambos mapas
        if (keyToValue.containsKey(key)) {
            V oldValue = keyToValue.get(key);
            valueToKey.remove(oldValue);
        }
        if (valueToKey.containsKey(value)) {
            K oldKey = valueToKey.get(value);
            keyToValue.remove(oldKey);
        }

        // Agregar las nuevas entradas
        keyToValue.put(key, value);
        valueToKey.put(value, key);

        return value;
    }

    @Override
    public V get(Object key) {
        return keyToValue.get(key);
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            return null;
        }
        V value = keyToValue.remove(key);
        if (value != null) {
            valueToKey.remove(value);
        }
        return value;
    }

    @Override
    public void clear() {
        keyToValue.clear();
        valueToKey.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public int size() {
                return keyToValue.size();
            }

            @Override
            public boolean isEmpty() {
                return keyToValue.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                    return keyToValue.containsKey(entry.getKey()) &&
                            keyToValue.get(entry.getKey()).equals(entry.getValue());
                }
                return false;
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof Map.Entry) {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                    K key = (K) entry.getKey();
                    V value = (V) entry.getValue();
                    if (keyToValue.containsKey(key) && keyToValue.get(key).equals(value)) {
                        BiMap.this.remove(key);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void clear() {
                BiMap.this.clear();
            }

            @Override
            public java.util.Iterator<Map.Entry<K, V>> iterator() {
                return new java.util.Iterator<>() {
                    private final java.util.Iterator<Map.Entry<K, V>> iterator = keyToValue.entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        return iterator.next();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    public K getKeyByValue(V value) {
        return valueToKey.get(value);
    }

    public Set<V> valuesSet() {
        return new HashSet<>(keyToValue.values());
    }

    @Override
    public String toString() {
        return "BiMap{" +
                "keyToValue=" + keyToValue +
                ", valueToKey=" + valueToKey +
                '}';
    }
}
