package rsl.util;

import java.util.concurrent.TimeUnit;

public class Timer {

    private long start = 0;
    private long stop = 0;


    public void start()
    {
        start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
    }

    public long stop()
    {
        stop = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        return getElapsedMillis();
    }

    public long getElapsedMillis()
    {
        return start - stop;
    }

    public static double millisToSeconds(long millis)
    {
        return millis/1000f;
    }

}
