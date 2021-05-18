package column_generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import column_generation.ColumGeneration.Column;

public class WalshAndPowel {
	public static Random rand = new Random();

	public static void stableDEST(Graph G, int n) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<Integer> listadj = new ArrayList<Integer>();

		int nb = 0;
		while (nb < n) {
			list.add(nb);
			for (int w : G.adj(nb)) {
				listadj.add(w);
			}
			for (int i = 0; i < n; i++) {
				if (!listadj.contains(i) && i != nb) {
					list.add(i);
				}
			}
			System.out.println("list: " + list);
			// colorie la list avec dsatur
			Integer[] arr = (Integer[]) list.toArray(new Integer[list.size()]);
			int[][] vecteur = GloutonSequentielletest(G, arr);
			for (int i = 0; i < arr.length; i++) {
				System.out.println(vecteur[i][0] + " " + vecteur[i][1]);
			}
			/// les stables :
			int nn = arr.length;
			int[][] tableau = new int[nn][3];
			for (int i = 0; i < nn; i++) {
				tableau[i][0] = vecteur[i][0];
				tableau[i][1] = vecteur[i][1];
				tableau[i][2] = 0;
			}
			List<Integer> stops_new_stable = new ArrayList<Integer>();
			int i = 0;
			while (i < nn) {

				int val = tableau[i][1];
				stops_new_stable.add(tableau[i][0]);
				if (tableau[i][2] == 0) {
					tableau[i][2] = 1;
					int j = i + 1;
					while (j < nn) {
						if (val == tableau[j][1] && tableau[j][2] == 0) {
							stops_new_stable.add(tableau[j][0]);
							tableau[j][2] = 1;
							j++;
						} else
							j++;

					}
					System.out.println("les stables: " + stops_new_stable);

				}
				i++;
				stops_new_stable.clear();
				stops_new_stable.add(tableau[0][0]);
			}

			nb++;
			list.clear();
			listadj.clear();
		}
	}

	public static int[][] GloutonSequentielletest(Graph G, Integer[] vect) {
		int n = vect.length;
		int[][] coloration = new int[n][2];
		for (int i = 0; i < n; i++) {
			coloration[i][0] = vect[i];
		}
		for (int i = 0; i < n; i++) {
			coloration[i][1] = 0;
		}
		int couleur = 1;
		int k = 0;
		while (k < n) {
			int i = 0;
			while (i < n) {
				boolean colorable = true;
				if (coloration[i][1] == 0) {
					for (int j = 0; j < i; j++) {
						int li = coloration[i][0];
						int col = coloration[j][0];
						for (int cols : G.adj(li)) {
							if (coloration[j][1] == couleur && col == cols)

								colorable = false;
						}
					}
				}

				else

					colorable = false;
				if (colorable == true) {
					coloration[i][1] = couleur;
					k++;
				}
				i++;
			}
			couleur++;
		}
		int color = couleur - 1;
		return coloration;
	}
	//////////////////////////////////////////////////

	public static ArrayList<Integer> stable(Graph G, int n) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<Integer> listadj = new ArrayList<Integer>();

		int nb = rand.nextInt(n);
		list.add(nb);
		for (int w : G.adj(nb)) {
			listadj.add(w);
		}
		for (int i = 0; i < n; i++) {
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
		for (int i : listadj) {
          System.out.print(i+" ");
		}
		System.out.println();
		return list;
	}

	public static int[][] GloutonSequentielle(Graph G, int n) {

		int[][] coloration = new int[n][2];
		for (int i = 0; i < n; i++) {
			coloration[i][0] = +i;
		}
		for (int i = 0; i < n; i++) {
			coloration[i][1] = 0;
		}
		int couleur = 1;
		int k = 0;
		while (k < n) {
			int i = 0;
			while (i < n) {
				boolean colorable = true;
				if (coloration[i][1] == 0) {
					for (int j = 0; j < i; j++) {
						int li = coloration[i][0];
						int col = coloration[j][0];
						for (int cols : G.adj(li)) {
							if (coloration[j][1] == couleur && col == cols)

								colorable = false;
						}
					}
				}

				else

					colorable = false;
				if (colorable == true) {
					coloration[i][1] = couleur;
					k++;
				}
				i++;
			}
			couleur++;
		}
		int color = couleur - 1;
		return coloration;
	}

	public static void main(String[] args) {
		System.out.println("Enter the name of the file :");
		Scanner sc = new Scanner(System.in);
		String file = sc.nextLine();
		In in = new In(file);
		Graph G = new Graph(in);
		System.out.println(G);
		int n = G.V();
		/*
		 * int[][] vecteur = GloutonSequentielle(G, n); int[][] tableau = new
		 * int[n][2]; for (int i = 0; i < n; i++) { tableau[i][0] =
		 * vecteur[i][1]; tableau[i][1] = 0; }
		 * 
		 * int i = 0; while (i < n) { List<Integer> stops_new_stable = new
		 * ArrayList<Integer>(); int val = tableau[i][0];
		 * stops_new_stable.add(i); if (tableau[i][1] == 0) { tableau[i][1] = 1;
		 * int j = i + 1; while (j < n) { if (val == tableau[j][0] &&
		 * tableau[j][1] == 0) { stops_new_stable.add(j); tableau[j][1] = 1;
		 * j++; } else j++;
		 * 
		 * } System.out.println("les stables: " + stops_new_stable);
		 * 
		 * } i++; }
		 */

		/*
		 * int iteration = 0; do { iteration++; //stable(G, n); stableDEST(G,n);
		 * } while (iteration < 20);
		 */
		ArrayList<Integer> list = new ArrayList<Integer>();
		list = stable(G, n);
		for (int i : list) {
			System.out.println( "List "+i + " ");
		}
		
		// stableDEST(G, n);
		// stable(G,n);
	}

}
