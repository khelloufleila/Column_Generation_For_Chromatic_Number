package column_generation;

/*
 * I have here the Master problem and the sub-problem 
 * 1) Solve the linear relaxation of the master problem 
 * 2) take the dual variables of the master problem 
 * 3) Update the value of the sub-problem 
 * 4) solve the subproblem as mixing to the program 
 * 5) use the result to generate a new column
 * 6) include that column into the master problem 
 * 7) Repeat this procedure until the objective value of the sub-problem is zero or positive 
 * */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import column_generation.ColumGeneration.Column;
//import column_generation.ColumGeneration.Monchoix;
import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.MIPCallback;
import ilog.cplex.IloCplex.Parameter;
import ilog.cplex.IloCplex.UnknownObjectException;

public class ColumGeneration {

	public MasterProblem masterproblem;
	public SubProblem subproblem;
	public Column column;
	public String instance;
	public Graph G;
	// public static Monchoix monchoix;
	public double ep = 0.000001;
	public Map<Integer, Double> Col_x;
	public Logger logger = new Logger();
	public List<Column> Columns = new ArrayList<Column>();
	// public ArrayList<IloRange> inegalites;
	// public Map<Integer, ArrayList<Integer>> all_Vertices = new
	// HashMap<Integer, ArrayList<Integer>>();
	public List<Integer> twoVertices = null;
	public Integer sommet1;
	public Integer sommet2;
	public int taille = 0;

	public ColumGeneration(String file, Graph g, In in) throws FileNotFoundException, IloException {
		this.instance = file;
		this.G = g;
		masterproblem = new MasterProblem();
		subproblem = new SubProblem();

	}

	public class Column {
		public IloNumVar x;
		public ArrayList<Integer> element;
		public int xi;
		public int cost = 1;
		public double value;

		public Column(List<Integer> addc) {

			element = new ArrayList<Integer>();
			for (int i = 0; i < addc.size(); i++) {
				element.add(addc.get(i));
			}
			xi = Columns.size();
			Columns.add(this);
			// System.out.println("Columns.size = " + xi);
		}

		public int a(int i) {
			return element.contains(i) ? 1 : 0;
		}

		public void DisplayInfo() {

			// System.out.print("[ xi: " + xi + " ] " + "Les elements de
			// ArrayList sont: ");
			for (int i = 0; i < element.size(); i++) {
				System.out.print(element.get(i) + " ");
			}
			System.out.println();
		}

	}

	public class MasterProblem {
		public IloCplex cplex;
		public IloObjective reduced_cost;
		public Column tousSommet;
		public double LastObjValue;
		public double value;
		public ArrayList<Double> pi = new ArrayList<Double>();
		// private List<IloConversion> mipConversion = new
		// ArrayList<IloConversion>();
		public Heuristics heuristic;
		private Map<Integer, IloRange> inegality = new HashMap<Integer, IloRange>();

		public MasterProblem() throws IloException {

			createModel();
			createDefaultStables();
			column_generation.Parameters.configureCplex(this);

		}

		private void createDefaultStables() {
			// System.out.println("Start creating the default columns");
			int n = G.V();

			int[] vecteur = heuristic.Dsatur(G, n);
			int[][] tableau = new int[n][2];
			for (int i = 0; i < n; i++) {
				tableau[i][0] = vecteur[i];
				tableau[i][1] = 0;
			}

			int i = 0;
			while (i < n) {
				List<Integer> stops_new_stable = new ArrayList<Integer>();
				int val = tableau[i][0];
				stops_new_stable.add(i);
				if (tableau[i][1] == 0) {
					tableau[i][1] = 1;
					int j = i + 1;
					while (j < n) {
						if (val == tableau[j][0] && tableau[j][1] == 0) {
							stops_new_stable.add(j);
							tableau[j][1] = 1;
							j++;
						} else
							j++;

					}
					// System.out.println("les stables: " + stops_new_stable);
					addNewColumn(new Column(stops_new_stable));
					taille++;
				}
				i++;

			}

		}

		private void createModel() throws IloException {

			cplex = new IloCplex();
			reduced_cost = cplex.addMinimize();
			for (int i = 0; i < G.V; i++) {
				inegality.put(i, cplex.addRange(1, Double.MAX_VALUE, "Col" + i));
			}
		}

		public void SolveRelaxation() {
			try {

				cplex.exportModel("titi.lp");
				if (cplex.solve()) {
					saveDualValues();
					LastObjValue = cplex.getObjValue();
					Col_x = new HashMap<Integer, Double>();
					for (Column Col : Columns) {
						if (cplex.getValue(Col.x) > 0) {
							double value = cplex.getValue(Col.x);
							Col_x.put(Col.xi, value);

							//Col.DisplayInfo();
						}
					}

				} else {
					LastObjValue = -1;
				}

			} catch (IloException e) {
				System.err.println("Concert exception caught in Solve Ralaxation: " + e);

			}
		}

		public void saveDualValues() {
			try {
				pi.clear();
				for (int i = 0; i < G.V(); i++) {
					pi.add(cplex.getDual(inegality.get(i)));

				}
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}

		}

		public void addNewColumn(Column col) {

			try {
				IloColumn new_Column = cplex.column(reduced_cost, col.cost);
				for (int i = 0; i < G.V(); i++) {
					new_Column = new_Column.and(cplex.column(inegality.get(i), col.a(i)));
				}
				col.x = cplex.numVar(new_Column, 0, 1, "S." + col.xi);

			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void DisplaySolution() {
			try {
				for (Column Col : Columns) {
					if (cplex.getValue(Col.x) > 0.99) {
						Col.DisplayInfo();
					}
				}

			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

	}

	public class SubProblem {
		public IloCplex cplex;
		public IloObjective reduced_cost;
		public IloIntVar[] x;
		public ArrayList<IloRange> constraints;
		public double LastObjValue;
		public double LastObjValueRelaxed;
		public Column tousSommet;
		// public MyMipCallBack mip_call_back;
		private Object Parameters;

		public SubProblem() throws IloException {
			this.constraints = new ArrayList<IloRange>();
			createModel();
			// Branch Here

			column_generation.Parameters.configureCplex(this);

		}

		private void createModel() throws IloException {
			cplex = new IloCplex();
			// variables
			x = new IloIntVar[G.V()];
			for (int u = 0; u < G.V(); u++) {
				x[u] = cplex.intVar(0, 1);
			}
			reduced_cost = cplex.addMaximize();
			for (int u = 0; u < G.V(); u++) {
				for (int w : G.adj(u)) {
					constraints.add(cplex.addLe(cplex.sum(cplex.prod(1, x[u]), cplex.prod(1, x[w])), 1,
							"ct" + constraints.size()));

				}
			}
			// cplex.exportModel("ColumnGen.lp");
		}

		public void updateReducedCost() throws IloException, IOException {
			IloNumExpr num_exp = cplex.linearNumExpr();
			num_exp = cplex.prod(x[0], 0);
			for (int i = 0; i < G.V(); i++) {
				num_exp = cplex.sum(num_exp, cplex.prod(this.x[i], ColumGeneration.this.masterproblem.pi.get(i)));
			}
			reduced_cost.clearExpr();
			reduced_cost.setExpr(num_exp);

		}

		public void Solve() {
			try {
				// mip_call_back.reset();
				if (cplex.solve()) {
					this.LastObjValue = cplex.getObjValue();
					this.LastObjValueRelaxed = cplex.getBestObjValue();
					if (1 - cplex.getObjValue() <= -0.00000001) {
						double val = 1 - cplex.getObjValue();
						// System.out.println("le cout réduit =" + val);
						SaveColumn();
					}
				}

			} catch (IloException e) {
				System.err.println("Error: " + e);
			}
		}

		public void SaveColumn() {
			try {

				List<Integer> addc = new ArrayList<Integer>();
				for (int i = 0; i < G.V(); i++) {
					if (cplex.getValue(x[i]) > 0.99999999) {
						addc.add(i);
					}
				}

				ColumGeneration.this.masterproblem.addNewColumn(new Column(addc));
				// System.out.println("the new column to add is :" + addc);

			} catch (IloException e) {
				System.out.println(".............." + e);
			}
		}
	}

	public boolean runColumnGeneration1() throws IloException, IOException {
		double result = 0;
		int iteration_counter = 0;
		do {
			iteration_counter++;
			masterproblem.SolveRelaxation();
			if (this.masterproblem.LastObjValue == -1)
				return false;
			subproblem.updateReducedCost();
			subproblem.Solve();
			displayIteration(iteration_counter);

		} while (subproblem.LastObjValue > 1.01);// && iteration_counter < 100);
		masterproblem.SolveRelaxation();
		if (this.masterproblem.LastObjValue == -1)
			return false;

		result = masterproblem.LastObjValue;
		for (Column Col : Columns) {
			Col.value = masterproblem.cplex.getValue(Col.x);
		}

		for (Column Col : Columns) {
			if (Col.value < 1 - ep && Col.value > ep)
				return false;
		}
		return true;

	}

	public void BranchementGauche(int v1, int v2) throws IloException {
		// fixer les bornes des colonnes (invalid => bornes à 0
		System.out.println("Branchement gauche de " + v1 + " " + v2);
		for (Column Col : Columns) {
			if (Col.a(v1) == 1 && Col.a(v2) == 0 || Col.a(v2) == 1 && Col.a(v1) == 0) {
				Col.x.setUB(0);

			}
		}
		// add the constraints x[v1]==x[v2]
		subproblem.constraints.add((IloRange) subproblem.cplex.addEq(subproblem.cplex.prod(1, subproblem.x[v1]),
				subproblem.cplex.prod(1, subproblem.x[v2]), "ctEquality" + subproblem.constraints.size()));

		// System.out.println("contrainte gauche ajouté!!!!!");
	}

	public void BranchementDroit(int v1, int v2) throws IloException {
		// System.out.println(" le branchement gauche add an Edge between this
		// two vertices v1 and v2");
		System.out.println("Branchement droit" + " de " + v1 + " " + v2);
		//masterproblem.cplex.exportModel("modelleft.lp");

		for (Column Col : Columns) {
			if (Col.a(v1) == 1 && Col.a(v2) == 1) {
				Col.x.setUB(0);
			}
		}
		// x[v1]+x[v2]<=1
		subproblem.constraints
				.add(subproblem.cplex.addLe(
						subproblem.cplex.sum(subproblem.cplex.prod(1, subproblem.x[v1]),
								subproblem.cplex.prod(1, subproblem.x[v2])),
						1, "ctAddEdge" + subproblem.constraints.size()));
		// System.out.println("contrainte droite ajouté!!!!!");
	}

	public boolean Entier(int u, int v) {
		// Add all the stables that contains two couple of vertex v1,v2 in S
		double sum = 0.0;
		for (Column Col : Columns) {
			for (Integer xi : Col_x.keySet()) {
				if (Col.xi == xi) {

					if (Col.a(u) == 1 && Col.a(v) == 1) {
						sum += Col_x.get(xi);
					}
				}
			}
		}

		if (sum < 1 && sum > 0) {
			// System.out.println("somme= " + sum);
			return true;// si la somme est fractionnaire
		}
		return false;
	}

	public void choixsommet() {
		for (int i = 0; i < G.V; i++) {
			for (int j = 0; j < G.V; j++) {
				if (i < j) {
					if (Entier(i, j)) {
						// System.out.println("i=" + i + " " + "j= " + j);
						sommet1 = i;
						sommet2 = j;
						return;
					}
				}
			}
		}

	}

	private void displayIteration(int iter) {
		if ((iter) % 20 == 0 || iter == 1) {
			System.out.println();
			System.out.print("Iteration");
			System.out.print("     Time");
			System.out.print("   column");
			System.out.print("       MP lb");
			System.out.print("       SB lb");
			System.out.print("      SB int");
			System.out.println();
		}
		System.out.format("%9.0f", (double) iter);
		System.out.format("%9.1f", logger.timeStamp() / 60);
		System.out.format("%9.0f", (double) Columns.size() - 1);
		System.out.format("%12.4f", masterproblem.LastObjValue);// master lower
																// // bound
		System.out.format("%12.4f", subproblem.LastObjValueRelaxed);// sb lower
																	// // bound
		System.out.format("%12.4f", subproblem.LastObjValue);// sb lower bound
		System.out.println();
	}
}
