package org.example.jowgo.utility;


public class Configuration
{
    public final int node_count;
    public final float edge_chance;

    Configuration(int node_count_, float edge_chance_)
    {
        node_count = node_count_;
        edge_chance = edge_chance_;
    }
}
