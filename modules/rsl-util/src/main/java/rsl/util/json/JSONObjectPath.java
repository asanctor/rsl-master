package rsl.util.json;

import java.util.Iterator;

public class JSONObjectPath implements Iterable<String>  {

    private String[] path = new String[0];

    public JSONObjectPath(String... path)
    {
        this.path = path;
    }

    public String[] getPath()
    {
        return path;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < path.length;
            }

            @Override
            public String next() {
                return path[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
