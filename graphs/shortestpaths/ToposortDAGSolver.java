package graphs.shortestpaths;

import graphs.Edge;
import graphs.Graph;

import java.util.*;

/**
 * Topological sorting implementation of the {@link ShortestPathSolver} interface for <b>directed acyclic graphs</b>.
 *
 * @param <V> the type of vertices.
 * @see ShortestPathSolver
 */
public class ToposortDAGSolver<V> implements ShortestPathSolver<V> {
    private final Map<V, Edge<V>> edgeTo;
    private final Map<V, Double> distTo;

    /**
     * Constructs a new instance by executing the toposort-DAG-shortest-paths algorithm on the graph from the start.
     *
     * @param graph the input graph.
     * @param start the start vertex.
     */
    public ToposortDAGSolver(Graph<V> graph, V start) {
        edgeTo = new HashMap<>();
        distTo = new HashMap<>();

        for (V vertex : getAllVertices(graph, start)) {
            distTo.put(vertex, Double.POSITIVE_INFINITY);
        }
        distTo.put(start, 0.0);

        //Use DFS
        List<V> reverseTopologicalOrder = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        dfsPostOrder(graph, start, visited, reverseTopologicalOrder);
        Collections.reverse(reverseTopologicalOrder);
        
        for (V vertex : reverseTopologicalOrder) {
        for (Edge<V> edge : graph.neighbors(vertex)) {
            relax(edge);
        }
    }
    }

    /**
     * Recursively adds nodes from the graph to the result in DFS postorder from the start vertex.
     *
     * @param graph   the input graph.
     * @param start   the start vertex.
     * @param visited the set of visited vertices.
     * @param result  the destination for adding nodes.
     */
    private void dfsPostOrder(Graph<V> graph, V start, Set<V> visited, List<V> result) {
        if (visited.contains(start)) {
            return;
        }
        visited.add(start);

        // Recursively visit all neighbors
        for (Edge<V> edge : graph.neighbors(start)) {
            dfsPostOrder(graph, edge.to, visited, result);
        }

        // Add the current vertex to the result
        result.add(start);
    }

    private void relax(Edge<V> edge) {
        V from = edge.from;
        V to = edge.to;
        double newDist = distTo.get(from) + edge.weight;

        if (newDist < distTo.get(to)) {
            distTo.put(to, newDist);
            edgeTo.put(to, edge);
        }
    }

    private Set<V> getAllVertices(Graph<V> graph, V start) {
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            V vertex = queue.poll();
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                for (Edge<V> edge : graph.neighbors(vertex)) {
                    queue.add(edge.to);
                }
            }
        }

        return visited;
    }

    @Override
    public List<V> solution(V goal) {
        List<V> path = new ArrayList<>();
        V curr = goal;
        path.add(curr);
        while (edgeTo.get(curr) != null) {
            curr = edgeTo.get(curr).from;
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
