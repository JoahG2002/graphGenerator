package org.example.jowgo;

import java.util.Optional;

import org.example.jowgo.utility.Parse;
import org.example.jowgo.utility.Constant;
import org.example.jowgo.utility.Configuration;
import org.example.jowgo.graph.UndirectedGraph;


public class Main
{
    public static void main(String[] args)
    {
        if (args.length != 4)
        {
            System.err.print(
                "\nInsufficient flags! Provide the number of nodes you want to generate and their chance of having a edge with another node:\n`java Main --nodes 1000 --chance-edge 0.1509`\n\n"
            );

            System.exit(Constant.FAILURE);
        }

        final Optional<Configuration> configuration = Parse.command_line_arguments(args);

        if (configuration.isEmpty())
        {
            System.err.print("\nInvalid arguments!\n\n");

            System.exit(Constant.FAILURE);
        }

        final UndirectedGraph graph = new UndirectedGraph(configuration.get());

        System.out.printf("\nConstructed: %s\n\n", graph.toString(100));

        graph.print_descriptives();

        System.exit(Constant.SUCCESS);
    }
}
