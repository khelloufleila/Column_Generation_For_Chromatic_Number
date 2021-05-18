package column_generation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import ilog.concert.IloException;

public class runColumnGeneration {

	public static void main(String[] args) throws Exception {

		// System.out.println("Enter the name of the file :");
		// Scanner sc = new Scanner(System.in);
		String file = "exemple.txt";// sc.nextLine();
		In in = new In(file);
		Graph G = new Graph(in);
		int n = G.V();
		System.out.println(G);
		// Heuristics heuristic = new Heuristics();
		// heuristic.Dsatur(G, n);
		ColumGeneration generation = new ColumGeneration(file, G, in);
		Branch2 branch = new Branch2(generation);
		boolean entier = branch.execution();
		System.out.println("entier=" + entier);
		System.out.println(branch.borneSuperieur);

		//bipartiInduit biparti = new bipartiInduit(generation);
		
		//biparti.Cyclics();
		
		
		
		/*
		 * biparti.Biparti(); for (int i : biparti.list) { System.out.print(i +
		 * " "); } System.out.println(); for (int j : biparti.listadj) {
		 * System.out.print(j + " "); }
		 */
	
		

	}

}
