package column_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

public class Graph {
	public static final String NEWLINE = System.getProperty("line.separator");

	public /* final */ int V;
	public int E;
	public Bag<Integer>[] adj;

	public Graph(int V) {

		if (V < 0)
			throw new IllegalArgumentException("Number of vertices must be nonnegative");
		this.V = V;
		this.E = 0;
		adj = (Bag<Integer>[]) new Bag[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new Bag<Integer>();
		}
	}

	public Graph(In in) {
		 this(in.readInt());
		
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException("Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = in.readInt() - 1;
			int w = in.readInt() - 1;
			validateVertex(v);
			validateVertex(w);
			//if (v < w) {
				addEdge(v, w);
			//}
		}
	}

	public Graph(Graph G) {
		this(G.V());
		this.E = G.E();
		for (int v = 0; v < G.V(); v++) {
			// reverse so that adjacency list is in same order as original
			Stack<Integer> reverse = new Stack<Integer>();

			for (int w : G.adj[v]) {
				reverse.push(w);
			}
			for (int w : reverse) {
				adj[v].add(w);
			}

		}
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}

	// throw an IndexOutOfBoundsException unless 0 <= v < V
	public void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
	}

	public void addEdge(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		E++;
		adj[v].add(w);
		adj[w].add(v);
	}

	public void addVertex(int v) {
		V++;

	}

	public Iterable<Integer> adj(int v) {
		validateVertex(v);
		return adj[v];
	}

	public int degree(int v) {
		validateVertex(v);
		return adj[v].size();
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " vertices, " + E + " edges " + NEWLINE);
		for (int v = 0; v < V; v++) {
			s.append(v + ": ");
			for (int w : adj[v]) {
				s.append(w + " ");
			}
			s.append(NEWLINE);
		}

		return s.toString();

	}

	public static void main(String[] args) {
		System.out.println("Enter the name of the file :");
		Scanner sc = new Scanner(System.in);
		String file = sc.nextLine();
		In in = new In(file);
		Graph G = new Graph(in);
		System.out.println(G);
		System.out.println("" + G.degree(0));
		System.out.println("le nombre de sommets ="+ G.V);
		/*
		 * for (int i = 0; i < G.V(); i++) { System.out.println("le degree de "
		 * + i + "est: " + G.degree(i)); }
		 */
		/*
		 * for (int i : G.adj(2)) { if (6 == i) { System.out.println(true);
		 * break; } // else System.out.println(false); }
		 */

		/*
		 * Map<Integer, ArrayList<Integer>> all_Vertices = new HashMap<Integer,
		 * ArrayList<Integer>>(); for (int i = 0; i < G.V; i++) {
		 * ArrayList<Integer> array = new ArrayList<Integer>(); for (int j :
		 * G.adj(i)) { array.add(j); } all_Vertices.put(i, array); } for (int i
		 * : all_Vertices.keySet()) { System.out.println(i +
		 * " and his list is:  " + all_Vertices.get(i)); }
		 */

	}
}
