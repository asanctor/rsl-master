package rsl.util.contentgenerator;

public class IDGenerator {

    private static long id = 0;

    public static long getID()
    {
        return ++id;
    }

    public static void reset()
    {
        id = 0;
    }
}
