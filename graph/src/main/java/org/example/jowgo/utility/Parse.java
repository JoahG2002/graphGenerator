package org.example.jowgo.utility;

import java.util.Optional;


public class Parse
{
    Parse() {}

    /**
     * Parses all command lines, and returns them as a `Configuration` instance on success.
     */
    public static Optional<Configuration> command_line_arguments(String[] argv)
    {
        byte i = 0;

        int node_count = 0;
        float chance_edge = 0.0f;

        while (i < argv.length)
        {
            if (argv[i].compareTo("--nodes") == Constant.SUCCESS)
            {
                node_count = Integer.parseInt(argv[i + 1]);

                i += 2;

                continue;
            }

            if (argv[i].compareTo("--chance-edge") == Constant.SUCCESS)
            {
                chance_edge = Float.parseFloat(argv[i + 1]);

                if ((chance_edge < 0.0f) || (chance_edge > 1.0f))
                    return Optional.empty();

                i += 2;

                continue;
            }

            i++;
        }

        if ((node_count == 0) || (chance_edge == 0.0))
            return Optional.empty();

        return Optional.of(new Configuration(node_count, chance_edge));
    }
}
