package column_generation;

import java.util.ArrayList;

import column_generation.ColumGeneration.Column;
import ilog.concert.IloException;

public class Branch2 {

	ArrayList<Noeud> Arbre;
	Integer borneSuperieur; // obtenu avec une heuristique
	Graph G;
	ColumGeneration BP;

	public Branch2(ColumGeneration BP) {
		this.BP = BP;
		this.borneSuperieur = BP.taille; // Integer.MAX_VALUE;
		this.G = this.BP.G;
		Arbre = new ArrayList<Noeud>();
	}

	public boolean comparaisonBornes() {
		if (borneSuperieur <= (int) (BP.masterproblem.LastObjValue + 0.9)) {
			return !false;
		} // >
		return !true;
	}

	public void miseAJour() throws IloException {
		for (Column x : this.BP.Columns) {
			x.x.setUB(1);
			x.x.setLB(0);
		}

		for (int i = 0; i < this.Arbre.size(); i++) {
			if (this.Arbre.get(i).gauche) {
				//System.out.println(this.Arbre.get(i).toString());
				BP.BranchementGauche(this.Arbre.get(i).u, this.Arbre.get(i).v);
			} else {
				//System.out.println(this.Arbre.get(i).toString());
				BP.BranchementDroit(this.Arbre.get(i).u, this.Arbre.get(i).v);
			}
		}

	}

	public boolean execution() throws IloException, Exception {

		boolean testStop = false;
		boolean debut = true;
		while (!testStop) {
			if (debut) {
				System.out.println("borne superieure = " + borneSuperieur);
				boolean entier = BP.runColumnGeneration1();
				if (entier) {
					borneSuperieur = (int) BP.masterproblem.LastObjValue;
					return true;
				}
				BP.choixsommet();
				int v1 = BP.sommet1;
				int v2 = BP.sommet2;
				Noeud nd = new Noeud(v1, v2, BP.masterproblem.LastObjValue, true);
				Arbre.add(nd);
				debut = false;
			} else {

				miseAJour();
				boolean entier = BP.runColumnGeneration1();
				boolean stop = comparaisonBornes();
				if (entier || stop || BP.masterproblem.LastObjValue == -1) {
					// System.out.println("entier " + entier + "\t" + "stop " +
					// stop);
					if (entier && BP.masterproblem.LastObjValue < borneSuperieur
							&& BP.masterproblem.LastObjValue != -1) {
						borneSuperieur = (int) (BP.masterproblem.LastObjValue + 0.1);
						// System.out.println("La Borne Superieur =" +
						// borneSuperieur);
					}
					while (!Arbre.isEmpty() && Arbre.get(Arbre.size() - 1).gauche == false) {
						Arbre.remove(Arbre.get(Arbre.size() - 1));
					}
					if (Arbre.isEmpty())
						return true;
					Arbre.get(Arbre.size() - 1).gauche = false;
				} else {
					// System.out.println("else entier " + entier + "\t" + "else
					// stop " + stop);
					BP.choixsommet();
					int v11 = BP.sommet1;
					int v21 = BP.sommet2;
					double lbd = BP.masterproblem.LastObjValue;
					Noeud e = new Noeud(BP.sommet1, BP.sommet2, lbd, true);
					Arbre.add(e);
				}
			}

			if (Arbre.size() == 0)
				testStop = true;
		}

		debut = false;
		return testStop = true;
	}

}
