package rsl.util.contentgenerator;

import org.fluttercode.datafactory.impl.DataFactory;

import java.util.Date;
import java.util.Random;

public class RandomTextFix {

    private static DataFactory df = new DataFactory();
    private static Random rand = new Random(new Date().getTime());

    public static String getRandomText(final int minLength, final int maxLength) {

        StringBuilder sb = new StringBuilder(maxLength);
        int length = minLength;
        if (maxLength != minLength) {
            length = length + rand.nextInt(maxLength - minLength);
        }
        while (length > 0) {
            if (sb.length() != 0) {
                sb.append(" ");
                length--;
            }
            final double desiredWordLengthNormalDistributed = 1.0 + Math.abs(rand.nextGaussian()) * 6;
            int usedWordLength = (int) (Math.min(length, desiredWordLengthNormalDistributed));
            String word = df.getRandomWord(usedWordLength);
            sb.append(word);
            length = length - word.length();
        }
        return sb.toString();

    }
}
