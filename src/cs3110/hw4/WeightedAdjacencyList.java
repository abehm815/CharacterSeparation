package cs3110.hw4;

import java.util.*;

import java.util.Comparator;

public class WeightedAdjacencyList<T> implements WeightedGraph<T> {
    /**
     * Adds the directed edge (u,v) to the graph. If the edge is already present, it should not be modified.
     * @param u The source vertex.
     * @param v The target vertex.
     * @param weight The weight of the edge (u,v).
     * @return True if the edge was added to the graph, false if 1) either u or v are not in the graph 2) the edge was already present.
     * 
     * @author Alex Behm
     */
	
	private final Map<T, Map<T, Integer>> adjList = new HashMap<>();
	private int edgeCount = 0;
	
	/**
     * Initializes a graph with the given list of vertices.
     */
	public WeightedAdjacencyList(List<T> vertices) {
	    for (T vertex : vertices) {
	        this.addVertex(vertex);
	    }
	}

	/**
     * Adds an edge from u to v with the given weight.
     * 
     * @return True if the edge is added successfully. False if u or v don't exist,
     *         the edge already exists, or the weight is negative.
     */
    @Override
    public boolean addEdge(T u, T v, int weight) {
        // If the graph doesn't contain one or both of the vertex we can't add an edge
    	if (!this.adjList.containsKey(u) || !this.adjList.containsKey(v) || weight < 0) return false;
    	if (u == null || v == null) return false;
    	
    	// Edge already exists
    	if (this.adjList.get(u).containsKey(v)) {
    		return false;
    	}
    	// Otherwise add the edge
    	this.adjList.get(u).put(v, weight);
    	this.edgeCount ++;
    	return true;
    }

    /**
     * Adds a vertex to the graph.
     * 
     * @param vertex A vertex to add to the graph.
     * @return False vertex was already in the graph, true otherwise.
     */
    @Override
    public boolean addVertex(T vertex) {
        if (this.adjList.containsKey(vertex) || vertex == null) {
        	return false;
        }
    	this.adjList.put(vertex, new HashMap<>());
    	
    	return true;
    }

    /**
     * @return |V|
     */
    @Override
    public int getVertexCount() {
        return this.adjList.size();
    }

    /**
     * @param v The name of a vertex.
     * @return True if v is in the graph, false otherwise.
     */
    @Override
    public boolean hasVertex(T v) {
        return (this.adjList.containsKey(v));
    }

    /**
     * @return An Iterable of V.
     */
    @Override
    public Iterable<T> getVertices() {
        return this.adjList.keySet();
    }

    /**
     * @return |E|
     */
    @Override
    public int getEdgeCount() {
        return this.edgeCount;
    }

    /**
     * @param u The source of the edge.
     * @param v The target of the edge.
     * @return True if (u,v) is in the graph, false otherwise.
     */
    @Override
    public boolean hasEdge(T u, T v) {
        return (this.adjList.containsKey(u) && this.adjList.get(u).containsKey(v));
    }

    /**
     * @param u A vertex.
     * @return The neighbors of u in the weighted graph.
     */
    @Override
    public Iterable<T> getNeighbors(T u) {
        if (!this.adjList.containsKey(u)) {
        	return Collections.emptySet();
        }
        
        return this.adjList.get(u).keySet();
    }

    /**
     * @param u
     * @param v
     * @return True if u and v are neighbors, false otherwise
     */
    @Override
    public boolean areNeighbors(T u, T v) {
        return hasEdge(u,v);
    }

    /**
     * Uses Dijkstra's algorithm to find the (length of the) shortest path from s to all other reachable vertices in the graph.
     * If the graph contains negative edge weights, the algorithm should terminate, but the return value is undefined.
     * @param s The source vertex.
     * @return A Mapping from all reachable vertices to their distance from s. Unreachable vertices should NOT be included in the Map.
     */
    @Override
    public Map<T, Long> getShortestPaths(T s) {
        // Initialize (G, s)
    	Map<T, Long> weightMap = new HashMap<>();
        weightMap.put(s, 0L);
        for (T v: this.getVertices()) {
        	if (!v.equals(s)) {
        		weightMap.put(v, Long.MAX_VALUE);
        	}
        }
        //Initialize Q and S
        Set<T> visited = new HashSet<>();
        PriorityQueue<Pair<T, Long>> q = new PriorityQueue<>(Comparator.comparingLong(Pair::getSecond));
        q.add(Pair.create(s, 0L));
        
        while (!q.isEmpty()) {
        	Pair <T, Long> current = q.poll();
        	
        	//S = S ∪ {u}
        	if (visited.contains(current.getFirst())) {
                continue; // already finalized, skip it
            }
            visited.add(current.getFirst());
            
            //for v ∈ G.adj(u)
        	for (T neighbor: this.getNeighbors(current.getFirst())) {
        		Long currentDistance = weightMap.get(current.getFirst());
        		int edgeWeight = this.adjList.get(current.getFirst()).get(neighbor);
        		
        		if (edgeWeight < 0) {
                    throw new IllegalArgumentException("Graph contains a negative edge weight.");
                }
        		
        		Long neighborDistance = weightMap.get(neighbor);

        		// v.d > u.d + w(u, v)
        		if (neighborDistance > currentDistance + edgeWeight) {
        		    Long newDistance = currentDistance + edgeWeight;
        		    weightMap.put(neighbor, newDistance);
        		    q.add(Pair.create(neighbor, newDistance));
        		}
        	}
        }
        
        //Remove any unreachable vertex before returning
        weightMap.values().removeIf(distance -> distance == Long.MAX_VALUE);
    	return weightMap;
    }
}
