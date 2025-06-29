package org.example.jowgo.graph;

import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import org.example.jowgo.utility.*;


public class UndirectedGraph
{
    private final int _m_node_count;
    private final double _m_chance_edge;

    private int _m_total_edge_count;
    private int _m_count_nodes_without_edges;

    private int[] _m_nodes;

    private List<List<Integer>> _m_edges;

    public UndirectedGraph(Configuration configuration)
    {
        _m_node_count = configuration.node_count;
        _m_chance_edge = configuration.edge_chance;

        _m_total_edge_count = 0;
        _m_count_nodes_without_edges = -1;

        _m_nodes = new int[_m_node_count];

        _m_edges = new ArrayList<List<Integer>>(_m_node_count);

        _fill_nodes();

        if (!_configuration_has_changed())
        {
            System.out.print("\nConfiguration has not changed; reading previous node and edge data.\n\n");

            _read_previous_edges();

            return;
        }

        System.out.print("\nPrevious not data found; generating nodes and edge data ...\n\n");

        _store_current_run_data();

        _generate_edges();

        _write_edges_to_txt();
    }

    /**
     * Returns whether the current configuration has changed compared to the previous configuration or not.
     */
    private boolean _configuration_has_changed()
    {
        File previous_run_data_file = new File(Constant.PATH_PREVIOUS_RUN_DATA_TXT);

        if (!previous_run_data_file.exists())
            return true;

        final Optional<String[]> file_lines = Read.file_lines(previous_run_data_file, 2);

        if (file_lines.isEmpty())
            return true;

        final String previous_node_count = file_lines.get()[Constant.INDEX_PREVIOUS_NODE_COUNT];

        if ((previous_node_count.isEmpty()) || (Integer.parseInt(previous_node_count) != _m_node_count))
            return true;

        final String previous_egde_change = file_lines.get()[Constant.INDEX_PREVIOUS_EDGE_CHANCE];

        if ((previous_egde_change.isEmpty()) || (Float.parseFloat(previous_egde_change) != _m_chance_edge))
            return true;

        return false;
    }
    /**
     * Writes the current run configuration data to a standard file, for them to be read in a future run.
     */
    private void _store_current_run_data()
    {
        Write.to_file(Constant.PATH_PREVIOUS_RUN_DATA_TXT, String.format("%d\n%f", _m_node_count, _m_chance_edge));
    }
    /**
     * Fills the array up with integers 0 to `_m_node_count - 1`.
     */
    private void _fill_nodes()
    {
        for (int i = 0; i < _m_node_count; i++)
        {
            _m_nodes[i] = i;

            _m_edges.add(new ArrayList<Integer>());
        }
    }
    /**
     * Reads all previous edges from the standard `.txt` file.
     */
    private void _read_previous_edges()
    {
        final File edges_file = new File(Constant.PATH_EDGES_TXT);

        try (BufferedReader file_reader = new BufferedReader(new FileReader(edges_file)))
        {
            String file_line;

            while ((file_line = file_reader.readLine()) != null)
            {
                String[] edge_nodes = file_line.split(",");

                final int node1 = Integer.parseInt(edge_nodes[0]);
                final int node2 = Integer.parseInt(edge_nodes[1]);

                if ((!has_node(node1)) || (!has_node(node2)))
                    continue;

                _m_edges.get(node1).add(node2);

                _m_total_edge_count++;
            }
        }
        catch (IOException e) { System.err.print("\nError edges from file lines: " + e.getMessage() + "\n\n"); }
    }
    /**
     * Writes all current edges to their target `.txt` file.
     */
    private void _write_edges_to_txt()
    {
        StringBuilder all_edges_as_string = new StringBuilder();

        int current_node = 0;

        for (List<Integer> edges_node : _m_edges)
        {
            if (edges_node.isEmpty())
            {
                current_node++;

                continue;
            }

            String current_node_as_string = Integer.toString(current_node);

            for (int node_edge_with : edges_node)
                all_edges_as_string.append(String.format("%s,%s\n", current_node_as_string, node_edge_with));

            current_node++;
        }

        Write.to_file(Constant.PATH_EDGES_TXT, all_edges_as_string.toString());
    }
    /**
     * Generates an N amount of edges, given a certain `_m_edge_chance`.
     */
    private void _generate_edges()
    {
        for (int index_node1 = 0; index_node1 < _m_node_count; index_node1++)
        {
            List<Integer> edges_node1 = _m_edges.get(index_node1);

            for (int index_node2 = (index_node1 + 1); index_node2 < _m_node_count; index_node2++)
            {
                if (Math.random() < _m_chance_edge)
                {
                    edges_node1.add(index_node2);

                    _m_total_edge_count++;
                }
            }
        }
    }
    /**
     * Prints all the graph's edges as a string to the standard output stream.
     */
    public String toString(int limit)
    {
        StringBuilder graph_as_string = new StringBuilder("UndirectedGraph(");

        int i = 0;
        int last_index = (_m_total_edge_count - 1);
        boolean limit_reached = false;

        for (int current_node = 0; current_node < _m_node_count; current_node++)
        {
            if (limit_reached)
                break;

            List<Integer> edges_node = _m_edges.get(current_node);

            if (edges_node.isEmpty())
                continue;

            String current_node_as_string = Integer.toString(current_node);

            for (int node_edge_with : edges_node)
            {
                if (i == limit)
                {
                    graph_as_string.append("...");

                    limit_reached = true;

                    break;
                }

                if (i != last_index)
                    graph_as_string.append(String.format("(%s, %s), ", current_node_as_string, node_edge_with));
                else
                    graph_as_string.append(String.format("(%s, %s)", current_node_as_string, node_edge_with));

                i++;
            }
        }

        graph_as_string.append(')');

        return graph_as_string.toString();
    }
    /**
     * Returns whether a node is contained in the graph.
     */
    public boolean has_node(int node) { return (node < _m_node_count); }
    /**
     * Returns all degrees of the graph, with degree at the index N being the degree of node N. This function implicitly counts the `_m_count_nodes_without_edges`.
     */
    public short[] get_degrees()
    {
        short[] degrees_nodes = new short[_m_node_count];

        if (_m_count_nodes_without_edges == -1)
        {
            for (int i = 0; i < _m_node_count; i++)
            {
                short edge_count_count = (short) _m_edges.get(i).size();

                degrees_nodes[i] = edge_count_count;

                if (edge_count_count == 0)
                    _m_count_nodes_without_edges++;
            }

            return degrees_nodes;
        }

        for (int i = 0; i < _m_node_count; i++)
            degrees_nodes[i] = (short) _m_edges.get(i).size();

        return degrees_nodes;
    }
    /**
     * Returns a read-only list of a node's edges.
     */
    final List<Integer> get_edges_node(int node) { return _m_edges.get(node); }
    /**
     * Returns the reach of each node in the graph.
     */
    public short[] get_reach_nodes()
    {
        int[] component_ids = new int[_m_node_count];
        Arrays.fill(component_ids, Constant.INVALID_INDEX);

        short current_component = 0;

        for (short start_index = 0; start_index < _m_node_count; start_index++)
        {
            if (component_ids[start_index] != Constant.INVALID_INDEX)
                continue;

            Queue<Short> queue = new LinkedList<Short>();
            queue.add(start_index);

            component_ids[start_index] = current_component;

            while (!queue.isEmpty())
            {
                short current_node = queue.poll();

                final List<Integer> edges_node = get_edges_node(current_node);

                for (Integer neighbour_node : edges_node)
                {
                    if (component_ids[neighbour_node] != Constant.INVALID_INDEX)
                        continue;

                    component_ids[neighbour_node] = current_component;

                    queue.add(neighbour_node.shortValue());
                }
            }

            current_component++;
        }

        short[] reach_per_node = new short[_m_node_count];
        Arrays.fill(reach_per_node, (short)0);

        for (int node1 = 0; node1 < _m_node_count; node1++)
        {
            for (int node2 = (node1 + 1); node2 < _m_node_count; node2++)
            {
                if (component_ids[node1] == component_ids[node2])
                    reach_per_node[node1]++;
            }
        }

        return reach_per_node;
    }
    public double clustering_coefficient(int node)
    {
        final List<Integer> neighbours_node = get_edges_node(node);

        if ((neighbours_node.isEmpty()) || (neighbours_node.size() < 2))
            return 0.0;

        final Set<Integer> unique_neighbours = new HashSet<Integer>(neighbours_node);

        int count_connections_under_neighbours = 0;
        long neighbour_count = neighbours_node.size();

        for (Integer neighbour_node : neighbours_node)
        {
            final List<Integer> neighbours_neighbour_node = get_edges_node(neighbour_node);

            if (neighbours_neighbour_node.isEmpty())
                continue;

            for (Integer potential_common_neighbour : neighbours_neighbour_node)
            {
                if (potential_common_neighbour == node)
                    continue;

                if (!unique_neighbours.contains(potential_common_neighbour))
                    continue;

                count_connections_under_neighbours++;
            }
        }

        return (
            ((double)count_connections_under_neighbours / 2.0) /
            (double)(neighbour_count * (neighbour_count - 1L) / 2L)
        );
    }
    /**
     * Returns the average clustering coefficient of the nodes in a graph: the chances of the neighbours of a node also having edges among each other.
     */
    public double average_clustering_coefficient()
    {
        double total_clustering_coefficient = 0.0;

        for (int node = 0; node < _m_node_count; node++)
            total_clustering_coefficient += clustering_coefficient(node);

        return (total_clustering_coefficient / (double)_m_node_count);
    }
    /**
     * Returns the density of the graph: the ratio of existing edges and the total amount of possible edges.
     */
    public double density()
    {
        if (_m_node_count < 2)
            return 0.0;

        return (
            2.0 * (double)(_m_total_edge_count / 2) / (double)(_m_node_count * (_m_node_count - 1))
        );
    }
    public int clique_count()
    {
        int clique_count_ = 0;

        for (int node = 0; node < _m_node_count; node++)
        {
            final List<Integer> neighbours_node = get_edges_node(node);

            if ((neighbours_node.isEmpty()) || (neighbours_node.size() < 2))
                continue;

            List<Integer> potential_clique = new ArrayList<Integer>(1);
            potential_clique.add(node);

            for (int neighbour_node : neighbours_node)
            {
                boolean all_connected = true;

                for (int member_node : potential_clique)
                {
                    if (member_node == neighbour_node)
                        continue;

                    boolean connected = false;

                    final List<Integer> neighbours_neighbour_node = get_edges_node(node);

                    if (neighbours_neighbour_node.isEmpty())
                        continue;

                    for (int neighbour_of_neighbour_node : neighbours_neighbour_node)
                    {
                        if (neighbour_of_neighbour_node == neighbour_node)
                        {
                            connected = true;
                            break;
                        }
                    }

                    if (!connected)
                    {
                        all_connected = false;
                        break;
                    }
                }

                if (all_connected)
                    potential_clique.add(neighbour_node);
            }

            if (potential_clique.size() < 2)
                clique_count_++;
        }

        return clique_count_;
    }

    /**
     * Returns the required amount of connections needed to make the graph a complete graph: a graph in which each node has an edge to each other node.
     */
    int count_connections_to_compleet() { return (((_m_node_count * (_m_node_count - 1)) / 2) - _m_node_count); }
    /**
     * Prints the descriptives of the graph to the standaard output stream.
     */
    public void print_descriptives()
    {
        final short[] degrees_graph = get_degrees();

        final short[] reach_per_node = get_reach_nodes();

        final DescriptiveShort node_reach_descriptives = Math_.get_descriptives(reach_per_node);
        final DescriptiveShort degree_descriptives = Math_.get_descriptives(degrees_graph);

        final double percentage_nodes_without_edges = Math_.percentage(_m_count_nodes_without_edges, _m_node_count);

        System.out.printf(
        """
        GRAPH DESCRIPTIVES:
        -----------------------------------
        - count nodes without connection %d (%f %%)
        - average clustering: %f;
        - density: %f;
        - clique count: %d;
        - minimum reach: %d;
        - maximum reach: %d;
        - average reach: %f;
        - mode reach: %d;
        - minimum degree: %d;
        - maximum degree: %d;
        - average degree: %f;
        - mode degree: %d;
        - standard deviation degree degree: %f;
        - count connections needed to complete graph: %d;
        """,
            _m_count_nodes_without_edges,
            percentage_nodes_without_edges,
            average_clustering_coefficient(),
            density(),
            clique_count(),
            node_reach_descriptives.minimum,
            node_reach_descriptives.maximum,
            node_reach_descriptives.mean,
            node_reach_descriptives.mode,
            degree_descriptives.minimum,
            degree_descriptives.maximum,
            degree_descriptives.mean,
            degree_descriptives.mode,
            degree_descriptives.stdev,
            count_connections_to_compleet()
        );
    }
}
