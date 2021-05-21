package rsl.core.coremodel;


import com.google.common.base.Objects;
import rsl.core.RSL;

import java.util.HashSet;
import java.util.Set;


public class MyClass {

    public static class Pair<K, V> {
        public final K key;
        public final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair<?, ?> p = (Pair<?, ?>) o;
            return Objects.equal(p.key, key) && Objects.equal(p.value, value);
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return "["+getKey()+","+getValue()+"]";
        }

    }

    public static Set<Preference> preferences = new HashSet<>();

    static void addPreferencePair(Preference<String, String> preference){
        preferences.add(preference);
    }

    static void removePreferencePair(Preference<String, String> preference) {
        preferences.remove(preference);
    }

    public static void main(String args[]) {
        /*Preference<String,String> x = new Preference("bla","yes");
        Preference<String,String> y = new Preference("bl","no");
        addPreferencePair(x);
        addPreferencePair(y);
        //removePreferencePair(x);
*/
        RSL.loadSchemaClasses("testschemas/");

        RslUser audrey = new RslUser();
        audrey.setUsername("audrey");
        audrey.save();

        RslEntity entityX = new RslEntity();
        entityX.setCreator(audrey);
        entityX.setName("test");
        entityX.save();

        int entity = audrey.getCreatedEntities().size();
        RslUser p = entityX.getCreator();
        String creator = p.getUsername();

        System.out.println(creator);
        System.out.println(entity);


        //System.out.println(preferences);
    }
}