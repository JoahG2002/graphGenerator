package org.example.jowgo.utility;


import java.util.HashMap;
import java.util.Map;


public class Math_
{
    private Math_() {}

    /**
     * Calculates the mean.
     */
    public static double mean(long sum, int n)
    {
        return (n == 0) ? 0.0 : ((double)sum / (double)n);
    }
    /**
     * Calculates the standard deviation of the values in an array.
     */
    public static double stdev(short[] array, double mean)
    {
        double square_difference_sum = 0.0;

        for (short value : array)
        {
            double difference = ((double)value - mean);

            square_difference_sum += (difference * difference);
        }

        return Math.sqrt(square_difference_sum / (double)array.length);
    }
    /**
     * Returns the percentage.
     */
    public static double percentage(int n, int total)
    {
        return (n == 0) ? 0.0 : (((double)n / (double)total) * 100.0);
    }
    /**
     * Return the most frequent key of a hash map.
     */
    public static Short mode(HashMap<Short, Integer> hash_map)
    {
        Short key_maximum = 0;
        Integer maximum_value = 0;

        for (final Map.Entry<Short, Integer> key_value_pair : hash_map.entrySet())
        {
            if (key_value_pair.getValue() < maximum_value)
                continue;

            maximum_value = key_value_pair.getValue();
            key_maximum = key_value_pair.getKey();
        }

        return key_maximum;
    }
    /**
     * Calculates all an array's value's descriptives in one loop.
     */
    public static DescriptiveShort get_descriptives(short[] array)
    {
        DescriptiveShort descriptives = new DescriptiveShort();

        long sum = 0L;

        descriptives.minimum = Short.MAX_VALUE;
        descriptives.maximum = Short.MIN_VALUE;

        HashMap<Short, Integer> value_frequency = new HashMap<Short, Integer>(array.length / 2);

        for (short value : array)
        {
            sum += value;

            if (value < descriptives.minimum)
                descriptives.minimum = value;

            if (value > descriptives.maximum)
                descriptives.maximum = value;

            value_frequency.put(value, (value_frequency.getOrDefault(value, 0) + 1));
        }

        descriptives.mean = mean(sum, array.length);
        descriptives.mode = mode(value_frequency);
        descriptives.stdev = stdev(array, descriptives.mean);

        return descriptives;
    }
}