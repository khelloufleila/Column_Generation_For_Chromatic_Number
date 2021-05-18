package column_generation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

//import column_generation.ColumGeneration;
import column_generation.ColumGeneration.Column;
//import column_generation.Graph;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloRange;
class node {
	int level;
	double bound;

}
public class BranchandBound {
	//public Graph G;
		// public Integer UpperBound;
		//public ColumGeneration BP;
		// cree un tableau
		public int[][] Tab_Adj;

		public int BranchAndBound(ColumGeneration BP, Graph G, int n) throws IloException, IOException {
			// Tab_Adj[i][j]=0 means we don't have any information about this two
			// vertices
			// Tab_Adj[i][j]=1 means that this two vertices have the same color
			// Tab_Adj[i][j]=-1 means that this two vertices have different colors
			Tab_Adj = new int[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					Tab_Adj[i][j] = 0;
				}
			}
			Stack<node> Q = new Stack<node>();
			node u = new node();
			node v = new node();
			v.level = 0;
			int chromatic = 0;
			int UpperBound = 5;
			Q.push(v);

			while (Q.size() > 0) {
				// fixer les bornes des colonnes (invalid => bornes à 0
				// pricing générer des colonnes aussi longtemps que l'on peut
				// bound?
				// ou solu réalisabl
				// définir sur quoi brancher
				// branch

				v = Q.pop();
				u = new node();
				if (v.level == 0) {
					u.level = 1;
				} else if (v.level != (n - 1)) {
					u.level = v.level + 1;
				}
				
				System.out.println("le branchement droit concatenate two vertices");
				int v1 = BP.twoVertices.get(0);
				int v2 = BP.twoVertices.get(1);

				// fixer les bornes des colonnes (invalid => bornes à 0
				
				for (Column Col : BP.Columns) {
					if (Col.a(v1) == 1 && Col.a(v2) == 0 || Col.a(v2) == 1 && Col.a(v1) == 0) {
						Col.x.setUB(0); 
						
					}else {
							Col.x.setUB(1);
							Col.x.setLB(0);
					}
				}
			
				
				//BP.subproblem.constraints
					//	.add((IloRange) BP.subproblem.cplex.addEq(BP.subproblem.cplex.prod(1, BP.subproblem.x[v1]),
							//	BP.subproblem.cplex.prod(1, BP.subproblem.x[v2])));
			    BP.subproblem.constraints.add((IloRange) BP.subproblem.cplex.addEq(BP.subproblem.cplex.prod(1, BP.subproblem.x[v1]),BP.subproblem.cplex.prod(1, BP.subproblem.x[v2]) , "ctEquality" + BP.subproblem.constraints.size()));
				Tab_Adj[v1][v2] = 1;
				
				//u.bound = BP.runColumnGeneration();
				
				System.out.println("right u = " + u.bound + " the level is u : " + u.level);
				// Verify if u.bound is integer or not
				//si les composantes de la solution sont entiere alors mettre a jour la borne 
				chromatic = (int) Math.floor(u.bound);
				if (chromatic == u.bound) {
					chromatic = (int) u.bound;
				}
				 BP.subproblem.cplex.exportModel("modelSubProblm1.lp");
				if (u.bound < UpperBound) {
					//delete inequality of subproblem
					int key=BP.subproblem.constraints.size();
					 BP.subproblem.constraints.remove(key-1);
					Q.push(u);
				}
				
			
				// ...............................................//
				u = new node();

				if (v.level == 0) {
					u.level = 1;
				} else if (v.level != (n - 1)) {
					u.level = v.level + 1;
				}

				System.out.println(" le branchement gauche add an Edge between this two vertices v1 and v2");
				
				BP.masterproblem.cplex.exportModel("modelleft.lp");
				Tab_Adj[v1][v2] = -1;
				for (Column Col : BP.Columns) {
					if (Col.a(v1) == 1 && Col.a(v2) == 1) {
						Col.x.setUB(0);
					}else{
						Col.x.setLB(0);
						Col.x.setUB(1);
					}
				}
				BP.subproblem.constraints.add(BP.subproblem.cplex.addLe(
						BP.subproblem.cplex.sum(BP.subproblem.cplex.prod(1, BP.subproblem.x[v1]),
								BP.subproblem.cplex.prod(1, BP.subproblem.x[v2])),
						1, "ctAddEdge" + BP.subproblem.constraints.size()));
			//	u.bound = BP.runColumnGeneration1(u);
	          //  BP.masterproblem.cplex.exportModel("model2");
				System.out.println("left v = " + u.bound + " the level is v : " + u.level);

				/*for (Column Col : BP.Columns) {
					for (Integer xi : BP.Col_x.keySet()) {

						if (Col.xi == xi) {
							System.out.println(" Left xi= " + xi + " the fractionnel value = " + BP.Col_x.get(xi));
							Col.DisplayInfo();
						}
					}
				}*/
				if (u.bound < UpperBound) {
					//i should to delete this one 
					int key=BP.subproblem.constraints.size();
					BP.subproblem.constraints.remove(key-1);
					Q.push(u);
				}
			}
			return chromatic;

		}
}
