package rsl.util;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class Random {

    private static final String letterCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String numberCharacters = "0123456789";
    private static SecureRandom rnd = new SecureRandom();

    public static String randomString(int length) { return randomString(length, false); }
    public static String randomString(int length, boolean lettersOnly){
        String chars = lettersOnly ? letterCharacters : letterCharacters + numberCharacters;
        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length; i++ )
            sb.append( chars.charAt( rnd.nextInt(chars.length()) ) );
        return sb.toString();
    }


    public static int randomInt(int min, int max)
    {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static long randomLong(long min, long max)
    {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

}
