package column_generation;

public class Noeud {
	double lb;
	int u, v;
	Boolean gauche;// true alors doit ouvrir à droite lors de la remonté
					// false doit remonter encore une fois

	public void setGauche(boolean gche) {
		gauche = gche;
	}

	public Noeud() {
		u = -1;
		v = -1;
		lb = -1.0;
		gauche = false;
	}

	public Noeud(int u, int v, double lb, boolean gauche) {
		this.u = u;
		this.v = v;
		this.lb = lb;
		this.gauche = gauche;
	}
	
	public String toString(){
		String s="";
		s+=u+" u\tv "+v+"\t"+gauche+"\t"+lb;
		return s;
	}
}
