package column_generation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class bipartiInduit {
	Graph G;
	ColumGeneration BP;
	ArrayList<Integer> list;
	ArrayList<Integer> listadj;
	public static Random rand = new Random();
	List<Integer> cycle;

	public bipartiInduit(ColumGeneration BP) {
		this.BP = BP;
		this.G = this.BP.G;
		list = new ArrayList<Integer>();
		listadj = new ArrayList<Integer>();

	}

	public void Biparti() {
		int nb = rand.nextInt(G.V);
		list.add(nb);
		for (int w : G.adj(nb)) {
			listadj.add(w);
		}
		for (int i = 0; i < G.V; i++) {
			if (!listadj.contains(i) && i != nb) {
				list.add(i);
			}
		}
		for (int i = 1; i < list.size(); i++) {
			int f = list.get(i);
			for (int w : G.adj(f)) {
				for (int j = i + 1; j < list.size(); j++) {
					if (list.get(j) == w) {
						list.remove(j);
					}
				}
			}

		}
		for (int i = 0; i < listadj.size(); i++) {
			int f = listadj.get(i);
			for (int w : G.adj(f)) {
				for (int j = i + 1; j < listadj.size(); j++) {
					if (listadj.get(j) == w) {
						listadj.remove(j);
					}
				}
			}

		}

	}

	public boolean adjacent(int u, List<Integer> cycle2) {
		for (int i = 0; i < cycle2.size(); i++) {
			if (u == cycle2.get(i))
				return true;
		}
		return false;
	}

	public void Cyclics() {
		for (int uk = 0; uk < G.V; uk++) {
			System.out.println("*******************");
			if (isCyclic()) {
				for (int i : cycle) {
					System.out.println("i= " + i);
				}
			}
			System.out.println("*******************");
		}
	}

	public boolean isCyclic() {
		cycle = new ArrayList<Integer>();
		boolean visited[] = new boolean[G.V];
		for (int i = 0; i < G.V; i++) {
			visited[i] = false;
		}
		for (int u = 0; u < G.V; u++) {
			if (!visited[u])
				cycle.add(u);
			System.out.println("----------------------------------");
			if (isCyclic(u, visited, -1))
				return true;
            System.out.println("----------------------------------");
		}
		return false;
	}

	public boolean isCyclic(int u, boolean[] visited, int parent) {
		visited[u] = true;
		Integer i;
		List<Integer> list0 = new ArrayList<Integer>();
		Iterator<Integer> it = G.adj[u].iterator();
		while (it.hasNext()) {

			// i don't want to take the first from the list
			for (int w : G.adj(u)) {

				list0.add(w);
			}
			int taille = list0.size() - 1;
			int rnd = rand.nextInt(taille);
			/////////////////////////////////////////////
			i = list0.get(rnd);
			// i=it.next();
			
				if (!visited[i]) {
					cycle.add(i);
					if (isCyclic(i, visited, u))
						return true;
				} else if (i != parent) {
					cycle.add(i);
					return true;
				}
			}
		return false;
	}

}
