package fr.ostracraft.towns.types;

import java.util.HashMap;

@SuppressWarnings({"unused", "unchecked"})
public class DatabaseResponse {

    private final HashMap<String, Object> hashMap;

    public DatabaseResponse(HashMap<String, Object> hashMap) {
        this.hashMap = hashMap;
    }

    public boolean isSet(String name) {
        return this.hashMap.containsKey(name);
    }

    public <T> T get(String name) {
        return (T) this.hashMap.get(name);
    }

    @Override
    public String toString() {
        return "DatabaseResponse{" +
                "hashMap=" + hashMap +
                '}';
    }
}
