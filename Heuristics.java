package column_generation;
/*
 * les différents étapes :
 * 1) Ordonner les sommets par ordre décroissant de degrés
 * 2) Colorer un sommet de degré maximum avec la couleur 1
 * 3) choisir un sommet avec DSAT maximum (en cas d'égalité, choisir un sommet de degré max)
 * 4) colorer ce sommet avec la plus petite couleur possible 
 * 5) si tous les sommets sont colorié alors stop 
 *   sinon aller en 3)
 *   **DSAT(v)= nombre de couleurs différentes dans le sommets adjacents à v*/
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Heuristics {

	public static int[] Dsatur(Graph G, int n) {
		int[] coloration = new int[n];
		int[] degre = new int[n];
		int col = 0;
		int[][] dsatur_colore = new int[n][2];
		int deg;
		for (int i = 0; i < n; i++) {
			deg = G.degree(i);
			// System.out.println("Error dans le remplissage de degre "+deg);
			degre[i] = G.degree(i);
			// System.out.print("degre de i: "+G.degree(i));
			dsatur_colore[i][0] = G.degree(i);
			dsatur_colore[i][1] = 0;
		}
		for (int i = 0; i < n; i++) {
			coloration[i] = 0;
		}
		int[] dsatur_max = new int[n];
		for (int i = 0; i < n; i++) {
			int k = 0, dsat_max = 0;
			for (int j = 0; j < n; j++) {
				if ((dsatur_colore[j][0] > dsat_max) && (dsatur_colore[j][1] != 1)) {
					k = 0;
					dsatur_max[k] = j;
					dsat_max = dsatur_colore[j][0];
					k++;
				} else {
					if ((dsatur_colore[j][0] == dsat_max) && (dsatur_colore[j][1] != 1)) {
						dsatur_max[k] = j;
						k++;
					}
				}
			}

			int deg_max = 0;
			//INT SOMMET=0;
			int sommet = 0;
			for (int j = 0; j < k; j++) {
				if (degre[dsatur_max[j]] > deg_max) {
					sommet = dsatur_max[j];
					deg_max = degre[dsatur_max[j]];
				}
			}

			int couleur = 1;
			boolean colorier = false;

			while (colorier == false) {
				colorier = true;
				for (int j = 0; j < n; j++) {
					for (int w : G.adj(j)) {
						if (w == sommet && coloration[j] == couleur)
							colorier = false;
					}
				}
				couleur++;
			}
			coloration[sommet] = couleur - 1;
			if (col < couleur - 1)
				col = couleur - 1;

			dsatur_colore[sommet][1] = 1;

			for (int j = 0; j < n; j++) {

				int[] couleur_utilisee = new int[n];
				for (int kk = 0; kk < n; kk++) {
					couleur_utilisee[kk] = 0;
				}

				for (int kk = 0; kk < n; kk++) {

					for (int w : G.adj(kk)) {
						if (w == kk && coloration[kk] != 0)
							couleur_utilisee[coloration[kk]] = 1;
					}
				}
				int dsat2 = 0;

				for (int kk = 0; kk < n; kk++) {
					if (couleur_utilisee[kk] == 1)
						dsat2++;
				}

				if (dsat2 > 0)
					dsatur_colore[j][0] = dsat2;
			}
		}
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
		int[] vecteur = Dsatur(G, n);
		for (int i = 0; i < n; i++) {
			System.out.println(i + "--" + vecteur[i]);
		}
		for (int i = 0; i < G.V(); i++) {
			System.out.println("le degree de " + i + "est: " + G.degree(i));
		}

	}
}
