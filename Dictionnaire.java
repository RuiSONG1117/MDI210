/*
 * author: Rui SONG
 * date: 11.10.2016
 * 
 * */
package simplexe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Scanner;

import simplexe.Dictionnaire;
import simplexe.MethodeEntrante;
import simplexe.Simplexe;

import matrice.Matrice;

;/**
 * Modelise un dictionnaire pour appliquer la methode du simplexe
 */
public class Dictionnaire extends Observable {
	// Les variables de decision (encore appelees variables principales) ont des numeros compris entre 1 et nbVarHorsBase
	// Les variables d'ecart ont des numeros compris entre nbVarHorsBase + 1 et this.nbVarBase + this.nbVarHorsBase
	// Pour ce qui concerne un dictionnaire de la premiere phase recherchant une solution realisable, 
	// une variable de numero 0 est ajoutee.
	private int nbVarHorsBase; // nombre de variables de decision (ou principales)
	private int nbVarBase;	// nombre de contraintes

	private int[] tabVarBase;  // numeros des variables de base, dans le tableau a partir de l'indice 1 (la case d'indice 0 ne sert pas)
	int[] tabVarHorsBase; // numeros des variables hors-bases, dans le tableau a partir de l'indice 1 (la case d'indice 0 ne sert pas)

	private double[][] dict;
	// Pour le dictionnaire concerne :
	// dict[0][0] contient la constante de la fonction z
	// pour j qui varie de 1 a nbVarHorsBase, dict[0][j] contient le coefficient dans la fonction z de la variable hors-base d'indice j 
	// pour i qui varie de 1 a nbVarBase, dict[i][0]contient le terme constant de l'expression de la variable de base d'indice i
	// pour i qui varie de 1 a nbVarBase et pour j qui varie de 1 a nbVarHorsBase, dict[i][j] contient, pour la variable de base
	// d'indice i, le coefficient de la variable hors-base d'indice j

	MethodeEntrante methode = MethodeEntrante.PREMIERE; // Indique la methode à utiliser pour choisir la variable entrante
	// Peut prendre les valeurs MethodeEntrante.PREMIERE, 
	// MethodeEntrante.PLUS_GRAND, MethodeEntrante.PLUS_AVANTAGEUSE;
	boolean bland; // si cette variable vaut true, on applique la regle de Bland
	boolean optimal; // passe à true si le dictionnaire est optimal
	boolean borne =  true; // passe à false si le probleme est non borne
	private boolean incomplet = false;
	public static double epsilon = 1E-12; // pour tester qu'un double est nul...

	/**
	 * Initialise le dictionnaire à partir de la lecture d'un fichier
	 * @param fichier le fichier de lecture
	 * @throws IOException
	 */
	public Dictionnaire(File fichier) throws IOException {
		lireFichier(fichier);
	}

	/**
	 * Construit un dictionnaire correspondant aux parametres.
	 * Connaissant le nombre de lignes et de colonnes, l'allocation des tableaux necessaires au
	 * codage est faite par ce constructeur.
	 * @param nbVarBase nombre de contraintes
	 * @param nbHorsbase nombre de variables de decision (variables principales)
	 * @param methode permet de preciser la methode à adopter pour le choix de la variable entrante
	 * @param bland permet de preciser si on utilise la regle de Bland
	 */
	public Dictionnaire(int nbVarBase, int nbHorsbase, MethodeEntrante methode, boolean bland) {
		this.nbVarBase = nbVarBase;
		this.nbVarHorsBase = nbHorsbase;
		this.methode = methode;
		this.bland = bland;
		this.allouer();
	}

	// DEBUT DES METHOSES A COMPLETER


	/**<font color = "red">
	 * A IMPLEMENTER EN PREMIER ; determine si le dictionnaire est realisable
	 * Il faut pour cela examiner dict[i][0] pour l'indice i qui varie de 1 a nbVarBase
	 * et utiliser la definition d'un dictionnaire realisable.
	 * @return renvoie true si le dictionnaire est realisable, renvoie false sinon
	 </font>*/
	public boolean estRealisable() {
		// Deux lignes suivantes a supprimer
		//Simplexe.sortie.println("Methode estRealisable a ecrire");
		//incomplet = true;
		for (int i=1; i <= nbVarBase;i++){
			if (dict[i][0]<0){
				return false;
			}
		}		
		// A modifier
		return true; 
	}

	/**<font color = "red">
	 * A IMPLEMENTER ; on cherche l'indice dans tabVarHorsBase de la "premiere" candidate a entrer en base 
	 * en considerant les variables dans l'ordre qu'elles occupent dans le tableau tabVarHorsBase.  
	 * Par exemple, supposons que dict[0][1] <= 0 et que dict[0][2] > 0. La methode retourne alors la valeur 2. 
	 * Si tabVarHorsBase[2] contient par exemple la valeur 3, cela signifie que la variable x3 est choisie comme 
	 * variable entrante mais cela n'intervient pas dans la programmation.  
	 * Pour cette recherche, on parcourt dict[0][j] pour j qui varie de 1 a nbVarHorsBase et on retient l'indice 
	 * correspondant au premier coefficient strictement positif rencontre s'il existe un tel indice ou bien quand la 
	 * fin de la ligne est atteinte. 
	 * Les numeros des variables n'importent pas.
	 * @return S'il existe une variable entrante, renvoie le plus petit indice d'une variable entrante, c'est-a-dire 
	 * le plus petit indice positif d'une case du tableau dict[0][...] contenant une valeur strictement positive.
	 * <br>sinon, renvoie la valeur 0 
	 </font>*/
	public int chercherPremierIndiceVariableEntrante() {
	// Deux lignes suivantes a supprimer
		//Simplexe.sortie.println("Methode chercherPremierIndiceVariableEntrante a ecrire");
		//incomplet = true;
		int iE;
		for(iE=1; iE<=nbVarHorsBase;iE++){
			if(dict[0][iE]>0){
				return iE;
			}
		}
		
		// A modifier
		return 0; 
	}

	/**<font color ="RED">
	 * A IMPLEMENTER ; une variable entrante etant choisie, cherche une variable sortante. 
	 * Les noms des variables n'importent pas.
	 * @param jE l'indice dans tabVarHorsBase de la variable entrante, i.e. l'indice de la colonne 
	 * de dict qui sera utilisee dans cette methode.
	 * Dans cette methode, on doit utiliser dict[i][0] et dict[i][jE] pour l'indice i qui varie de 1 a nbVarBase.
	 * @return Si le dictionnaire montre que le probleme n'est pas borne, renvoie 0.
	 * 		   Sinon, renvoie l'indice dans tabVarBase d'une variable sortante, i.e. l'indice de la ligne de 
	 * 		   la matrice dict correspondant a la variable sortante.
	 </font>*/
	public int chercherIndiceVariableSortante(int jE)  {
		// Trois lignes suivantes a supprimer	
		//Simplexe.sortie.println("\nLa variable entrante : x" + this.tabVarHorsBase[jE]);
		//Simplexe.sortie.println("Methode chercherIndiceVariableSortante a ecrire");
		//incomplet = true;
		double rapportMin = Double.MAX_VALUE;
		int iS = 0;
		for(int i=1; i<=nbVarBase; i++){
			if(dict[i][jE]<0){
				double rapport = -1*dict[i][0]/dict[i][jE];
				if(rapport<rapportMin){
					rapportMin=rapport;
					iS = i;
				}
			}
		}
		
		// A modifier
		return iS; 
	}
	
	/**<font color ="red">
	 * A IMPLEMENTER ; modifie le dictionnaire courant (reference par this) en pivotant selon 
	 * les parametres indiques.
	 * @param iS l'indice dans tabVarBase de la variable sortante (i.e. indice dans coeff de la ligne a partir
	 * de laquelle on pivote)
	 * @param jE l'indice dans varHorBase de la variable entrante (i.e. indice dans coeff de la colonne a partir
	 * de laquelle on pivote)
	 </font>*/
	/* 
	 * Modifie le dictionnaire en faisant entrer la variable entrante et sortir la variable sortante.
	 * Si le dictionnaire est le suivant (code dans dico1.txt): 	
	 * x3 = 24.0 - 2,00x1 - 3,00x2
	 * x4 = 30.0 - 5,00x1 - 3,00x2
	 * x5 = 18.0 - 1,00x1 - 3,00x2
	 * z = 0.0 + 4,00x1 + 3,00x2
	 * et si x1 (a l'indice 1 de tabVarHorsBase) entre en base et que 
	 * x4 (a l'indice 2 de tabVarBase) sort, la methode pivoter
	 * est invoquee avec le parametre jE qui vaut 1 et le parametre iS qui vaut 2.
	 * Apres avoir pivote, le dictionnaire doit etre :
	 * x3 = 12 + 0,4 x4 - 1,8 x2
	 * x1 =  6 - 0,2 x4 - 0,6 x2
	 * x5 =  12 + 0,2 x4 - 2,4 x2
	 * z  = 24 - 0,8 x4 + 0,6 x2
	 * D'ou le codage du dictionnaire modifie :
	 * tabVarHorsBase contient aux indices 1 et 2 les numeros 4 puis 2
	 * tabVarBase contient aux indices 1, 2 et 3, les numeros 3 puis 1 puis 5
	 * Le tableau dict est contient : 12, 6, 12
	 * 24 -0,8  0,6
	 * 12  0,4 -1,8
	 *  6 -0,2 -0,6
	 * 12  0,2 -2,4 
	 */
	public void pivoter(int iS, int jE) {
	// Deux lignes suivantes a supprimer
		//Simplexe.sortie.println("Methode pivoter a ecrire");
		//incomplet = true;
		double k = -1/dict[iS][jE];// 1/5
		for(int j=0;j<=nbVarHorsBase;j++){
			if(j==jE){
				dict[iS][j]=-k;
			}else{
				dict[iS][j]*=k;
			}
		}
		for(int i=0;i<=nbVarBase;i++){
			if(i!=iS){
				double coeff = dict[i][jE];
				for(int j=0;j<=nbVarHorsBase;j++){
					dict[i][j]+=coeff*dict[iS][j];
				}
				dict[i][jE] = coeff*dict[iS][jE];
			}
		}
		// changer les indices
		int temp = tabVarBase[iS];
		tabVarBase[iS] = tabVarHorsBase[jE];
		tabVarHorsBase[jE] = temp;
	}


	/**<font color ="RED">
	 * A IMPLEMENTER ; cherche une variable entrante de plus grand coefficient dans la fonction objectif du dictionnaire. 
	 * Les numeros des variables n'importent pas. Seul dict[0][j] pour j qui varie de 1 a nbVarHorsBase est concerne.
	 * @return S'il existe une variable entrante, renvoie l'indice dans tabVarHorsBase de la variable entrante 
	 * de plus grand coefficient, i.e. l'indice positif dans dict[0][...] de la case de plus grande valeur.
	 * <br>sinon, renvoie la valeur 0 
	 </font>*/
	public int chercherIndiceVariableEntranteGrandCoeff() {	
		// Deux lignes a supprimer
		//Simplexe.sortie.println("Methode chercherIndiceVariableEntranteGrandCoeff a ecrire");
		//incomplet = true;
		double coeffMax = Double.MIN_VALUE;
		int jE=0;
		for (int j=0;j<=nbVarHorsBase;j++){
			if(dict[0][j]>0){
				if (dict[0][j]>coeffMax){
					jE = j;
					coeffMax = dict[0][j];
				}
			}
		}
		
		// A modifier
		return jE;
	}

	/**<font color ="RED">
	 * A IMPLEMENTER ; recherche une variable entrante qui maximise l'augmentation de la fonction objectif dans le 
	 * prochain dictionnaire. Les noms des variables n'importent pas. 
	 * @return 
	 * S'il n'existe pas de variable entrante, renvoie la valeur 0;
	 * <br>sinon s'il existe une variable entrante qui donne une augmentation infinie (probleme non borne),
	 * renvoie la valeur -1 ;
	 * <br>sinon renvoie l'indice dans tabVarHorsBase de la variable entrante qui maximise l'augmentation de la fonction objectif
	 * dans le prochain dictionnaire.
	 </font>*/
	public int chercherIndiceVariableEntranteAvantageuse() {	
		// Deux lignes a supprimer
		//Simplexe.sortie.println("Methode chercherIndiceVariableEntranteAvantageuse a ecrire");
		//incomplet = true;
		int jE = 0;
		double avantageMax = Double.MIN_VALUE;
		for(int j=1; j<=nbVarHorsBase;j++){
			if(dict[0][j]>0){
				int iS = chercherIndiceVariableSortante(j);
				if(iS==0){
					return -1;	// probleme non borne
				}else{
					double avantage = dict[iS][0]*dict[0][j];
					if(avantage>avantageMax){
						avantageMax = avantage;
						jE = j;
					}
				}
			}
		}
		
		// A modifier
		return jE;
	}

	/**<font color ="RED">
	 * A IMPLEMENTER : cherche l'indice de la variable entrante de plus petit numero (la variable x_i
	 * a pour numero i). Sert quand on utilise la regle de Bland.
	 * <br>Par exemple, si tabVarHorsBase contient a partir de l'indice 1 les numeros 6, 7, 2, 3, 5 et si les variables candidates 
	 * a entrer sont les variables d'indice 2 et 4 (tabVarHorsBase[2] vaut 7, tabVarHorsBase[4] vaut 3), la valeur retournee est 4.  
	 * @return S'il existe une variable entrante, renvoie l'indice dans tabVarHorsBase de la variable entrante de plus petit numero.
	 * 		   <br>Sinon, renvoie la valeur 0.
	 </font>*/
	public int chercherIndiceVariableEntrantePlusPetitNumero(){		
		// Deux lignes a supprimer
		//Simplexe.sortie.println("Methode chercherIndiceVariableEntrantePlusPetitNumero a ecrire");
		//incomplet = true;
		int plusPtNum = Integer.MAX_VALUE;
		int jE = 0;
		for(int j=1;j<=nbVarHorsBase;j++){
			if(dict[0][j]>0){
				if(tabVarHorsBase[j]<plusPtNum){
					plusPtNum = tabVarHorsBase[j];
					jE=j;
				}
			}
		}
		
		// A modifier
		return jE;
	}


	/**<font color ="RED">
	 * A IMPLEMENTER ; une variable entrante etant choisie, cherche l'indice de la variable sortante de plus petit numero 
	 * (la variable x_i a pour numero i). Sert quand on utilise la regle de Bland.
	 * <br>Par exemple, si tabVarBase contient a partir de l'indice 1 les numeros 4, 5, 1, 2, 7 et si les variables candidates à sortir sont
	 * les variables d'indice 2 et 4 (tabVarBase[2] vaut 5,  tabVarBase[4] vaut 2), la valeur retournee est 4.  
	 * @param jE l'indice de la variable entrante.
	 * @return Si le dictionnaire montre que le probleme n'est pas borne, renvoie -1.
	 * 		   <br>Sinon, renvoie l'indice de la variable sortante.
	 </font>*/
	public int chercherIndiceVariableSortantePlusPetitNumero(int jE) {		
		// Deux lignes a supprimer
		//Simplexe.sortie.println("Methode chercherIndiceVariableSortantePlusPetitNumero a ecrire");
		//incomplet = true;
		//System.out.println("bland?");
		int iS = 0;
		double rapportMin = Double.MAX_VALUE;
		for(int i=0; i<=nbVarBase;i++){
			if(dict[i][jE]<0){
				double rapport = -1*dict[i][0]/dict[i][jE];
				if (rapport<rapportMin){
					rapportMin = rapport;
					iS=i;
				}else if(rapport == rapportMin){
					if(i<iS){
						iS = i;
					}
				}
			}
		}
		
		// A modifier
		if (rapportMin == Double.MAX_VALUE){
			return -1;
		}else{
			return iS;
		}
		
	}
	// FIN DES METHOSES A COMPLETER
	
	public int chercherIndiceVariableEntrante(MethodeEntrante methode){
		switch(methode) {
		case PREMIERE :
			return chercherPremierIndiceVariableEntrante();
		case PLUS_GRAND :
			return chercherIndiceVariableEntranteGrandCoeff();
		case PLUS_AVANTAGEUSE  :
			return chercherIndiceVariableEntranteAvantageuse();
		}
		return 0;
	}


	/**
	 * Effectue une etape de la methode du simplexe.
	 * Si le dictionnaire est optimal, positionne l'attribut optimal à true
	 * <br>Si l'etape a montre que le probleme est non borne, positionne l'attribut borne à false 
	 * <br>Dans les autres cas, effectue une etapte de la methode du simplexe en choisissant une 
	 * variable entrante, une variable sortante et en pivotant.  
	 */
	public void uneEtape() {		
		int jE;

		if (incomplet) return;
		jE = chercherIndiceVariableEntrante(methode);
		if (incomplet) {
			return;
		}
		if (jE == 0) {
			optimal = true;
		}
		else if (jE == -1) {
			borne = false;
		}
		else {
			uneEtape(jE);
		}
	}

	public void uneEtape(int jE) {
		int iS;
		if (dict[0][jE] <= 0) {			
			Simplexe.sortie.println("La variable indiquee comme entrante n'est pas correcte");
			return;
		}
		iS = chercherIndiceVariableSortante(jE);
		if (incomplet) return;
		if (iS == 0) {
			borne = false;
		}
		else {
			if (bland  && dict[iS][0] == 0) {
				jE = chercherIndiceVariableEntrantePlusPetitNumero();
				if (incomplet) return;
				if (jE == 0) {
					optimal = true;
					return;
				}
				iS = chercherIndiceVariableSortantePlusPetitNumero(jE);
				if (incomplet) return;
				if (iS == 0) {
					borne = false;
					return;
				}
			}
			Simplexe.sortie.println("\nLa variable entrante : x" + this.tabVarHorsBase[jE]);
			Simplexe.sortie.println("La variable sortante : x" + this.tabVarBase[iS]);
			uneEtape(jE, iS);
		}
	}


	public void uneEtape(int jE, int iS) {
		pivoter(iS, jE);
		if (incomplet) return;
		Simplexe.sortie.afficherDico(this);
	}


	/**<font color = "red">
	 * A IMPLEMENTER ; calcule un premier dictionnaire, non realisable, pour la premiere phase.  
	 * <br>La variable introduite s'appellera x0 (autrement dit aura le numero 0) et 
	 * on suppose qu'aucune autre variable a le numero 0.
	 * @return renvoie un premier dictionnaire, non realisable, pour la premiere phrase. 
	 */
	public Dictionnaire premierDicoPbAuxiliaire() {
		Dictionnaire dico = new Dictionnaire(this.nbVarBase, this.nbVarHorsBase + 1, methode, bland);

		// Initialisation des variables de base et hors-base
		dico.tabVarHorsBase[this.nbVarHorsBase + 1] = 0;
		for (int j = 1; j <= this.nbVarHorsBase; j++) dico.tabVarHorsBase[j] = this.tabVarHorsBase[j];
		for (int i = 1; i <= this.nbVarBase; i++) dico.tabVarBase[i] = this.tabVarBase[i];

		// Initialisation des constantes
		for (int i = 1; i <= this.nbVarBase; i++) dico.dict[i][0] = this.dict[i][0];

		// Initialisation des coefficients
		for (int i = 1; i <= this.nbVarBase; i++) {
			for (int j = 1; j <= this.nbVarHorsBase; j++) dico.dict[i][j] = this.dict[i][j];
		}

		for (int i = 1; i <= nbVarBase; i++) {
			if (dico.dict[i][0] < 0) dico.dict[i][dico.nbVarHorsBase] = 1;
		}		
		dico.dict[0][dico.nbVarHorsBase] = -1;
		Simplexe.sortie.afficherDico(dico);

		return dico;
	}

	/**<font color = "red">
	 * Le dictionnaire reference par this est le premier dictionnaire, non realisable,
	 * de la premiere phase (le dictionnaire obtenu par la methode premierDicoPbAuxiliaire) ; 
	 * modifie ce dictionnaire pour avoir un premier dictionnaire realisable  pour la premere phase. 
	 </font>*/
	public void dicoRealisablePbAuxiliaire() {
		double min = 0;
		int iS = -1;

		for (int i = 1; i <= nbVarBase; i++) {
			double constante = this.dict[i][0];
			if (constante < min) {
				min = constante;
				iS = i;
			}
		}	
		Simplexe.sortie.println("Variable entrante : x0");
		Simplexe.sortie.println("Variable sortante : x" + this.tabVarBase[iS]);
		pivoter(iS, this.nbVarHorsBase);
		if (incomplet)return;

		Simplexe.sortie.afficherDico(this);
	}
	/**
	 * A partir d'un dictionnaire de la premiere phase (dictionnaire du probleme auxiliaire) tel que la valeur de la
	 * fonction objectif dans la solution basique associee est 0 (ce qui montre que le probleme initial est realisable), 
	 * calcule le premier dictionnaire de la seconde phase. Ne change pas le dictionnaire. Pour cela :
	 * <br>- supprime la variable x0 (le nombre colonnes diminue de 1);
	 * <br>- calcule la fonction objectif en fonction des variables hors-bases
	 * @param zInitial la partie lineaire de la fonction objectif exprimee en fonction des variables de decision
	 * @param z0Initial la constante de la fonction objectif exprimee en fonction des variables de decision
	 * @param varHorsBaseInitiale les variables hors base avant de commencer la premiere phase
	 * @return renvoie le dictionnaire obtenu donnant le dictionnaire initial de la seconde phase
	 */
	public Dictionnaire dicoPbSecondePhase(double[] zInitial, double z0Initial, int[] varHorsBaseInitiale) {
		Dictionnaire dico = new Dictionnaire(nbVarBase, nbVarHorsBase - 1, methode, bland);
		int colonneX0 = 0;
		int variable, ligne, colonne;
		double multiplicateur;

		for (int j = 1; j <= nbVarHorsBase; j++)
			if (tabVarHorsBase[j] == 0) {
				colonneX0 = j;
				break;
			}

		if (colonneX0 == 0) return null;
		// Initialisation des variables de base et hors-base
		for (int j = 1; j < colonneX0; j++) dico.tabVarHorsBase[j] = tabVarHorsBase[j];
		for (int j = colonneX0; j < nbVarHorsBase; j++) dico.tabVarHorsBase[j] = tabVarHorsBase[j + 1];

		for (int i = 1; i <= nbVarBase; i++) dico.tabVarBase[i] = tabVarBase[i];

		// Initialisation des constantes
		for (int i = 1; i <= nbVarBase; i++) dico.dict[i][0] = dict[i][0];

		// Initialisation des coefficients
		for (int i = 1; i <= nbVarBase; i++) {
			for (int j = 1; j < colonneX0; j++) dico.dict[i][j] = dict[i][j];
			for (int j = colonneX0; j < nbVarHorsBase; j++)dico.dict[i][j] = dict[i][j + 1];
		}

		dico.dict[0][0] = z0Initial;
		/* 
		 * zInitial[j], est le coefficient de z de la variable de numero j + 1 dans le dictionnaire initial
		 * On calcule la contribution de zInitial[j]X_(j+1) dans le nouveau z
		 */
		for (int j = 1; j <= dico.nbVarHorsBase; j++) {
			variable = varHorsBaseInitiale[j];
			ligne = dico.indiceBase(variable);

			if (ligne == 0) {
				colonne = dico.indiceHorsBase(variable);
				dico.dict[0][colonne] += zInitial[j]; 
			}
			else {
				multiplicateur = zInitial[j];
				for (int k = 0; k <= dico.nbVarHorsBase; k++)
					dico.dict[0][k] += multiplicateur * dico.dict[ligne][k];
			}
		}	
		Simplexe.sortie.println("Dictionnaire realisable pour le probleme initial :");
		Simplexe.sortie.afficherDico(dico);
		return dico;
	}

	/**
	 * Cherche si la variable indiquee en parametre est en base.
	 * @param var le numero de la variable recherchee.
	 * @return si la variable indiquee en parametre est en base, renvoie l'indice de
	 * cette variable dans tabVarBase (entre 0 et nbVarBase - 1); sinon, renvoie -1.
	 */
	public int indiceBase(int var) {
		for (int i = 1; i <= nbVarBase; i++) if (tabVarBase[i] == var) return i;
		return 0;
	}

	/**
	 * Cherche si la variable indiquee en parametre est hors-base.
	 * @param var le numero de la variable recherchee
	 * @return si la variable indiquee en parametre est hors-base, renvoie l'indice 
	 * de cette variable (entre 0 et nbVarHorsBase - 1) ; sinon, renvoie -1
	 */
	public int indiceHorsBase(int var) {
		for (int j = 1; j <= nbVarHorsBase; j++) if (tabVarHorsBase[j] == var) return j;
		return 0;
	}

	/**
	 * 
	 * @param listeBase liste des numeros de variable qu'on souhaite en base
	 * @return les deux listes dans un tableau d'ArrayList des indices des varaibles entrantes et des
	 * indices des variables sortantes
	 */
	public ArrayList<Integer>[] determinerSortants(ArrayList<Integer> listeBase) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] listes = (ArrayList<Integer>[]) new ArrayList[2];
		ArrayList<Integer>listeSortants = listes[0] = new ArrayList<Integer>();
		ArrayList<Integer> listeEntrants = listes[1] = new ArrayList<Integer>();;

		int iS = 0, jE;

		for (Integer numE : listeBase) {
			jE = indiceHorsBase(numE);
			if (jE != -1) {// la variable de numero numE est hors base, à l'indice jE
				listeEntrants.add(numE); // la variable de numero numE doit entrer
				while (listeBase.contains(tabVarBase[iS])) iS++;
				listeSortants.add(tabVarBase[iS]);
				iS++;
			}
		}
		return listes;
	}

	/**
	 * pivote en considerant les numeros des variables entrantes et sortantes
	 * @param numE le numero de la variable entrante
	 * @param numS le numero de la variable sortante
	 */
	public void pivoterSelonNumeros(int numS, int numE) {
		pivoter(indiceBase(numS), indiceHorsBase(numE));
	}

	/**
	 * Alloue l'espace memoire necessaire pour coder le dictionnaire.
	 */
	public void allouer(){
		tabVarBase = new int[nbVarBase + 1];
		tabVarHorsBase = new int[nbVarHorsBase + 1];
		dict = new double[nbVarBase + 1][nbVarHorsBase + 1];
	}

	public Matrice calculerB(ArrayList<Integer> listeBase ) {
		Matrice B = new Matrice(nbVarBase, nbVarBase);
		int j, col = 0;

		for (int num : listeBase) {
			j = indiceHorsBase(num);
			if (j != -1) 
				for (int k = 0; k < nbVarBase; k++)
					B.setValeur(k, col, -dict[k][j]);
			else  {
				for(int k = 0; k < nbVarBase; k++) B.setValeur(k, col, 0);
				B.setValeur(indiceBase(num),  col,  1);
			}
			col++;
		}
		Simplexe.sortie.println(B.toString());
		return B;
	}

	/** 
	 * Calcule un dictionnaire à partir d'un fichier.
	 */
	/* Initialise nbVar, nbContraintes, coeff, tabVarBase, tabVarHorsBase
	 Le fichier correspond toujours à un probleme mis sous forme standard
	 Si le pb est 
	 maximiser z = 4x1 + 3 x2 
	 avec  
	      2x1 + 3x2 <= 24
	      5x1 + 3x2 <= 30
	      x1  + 3x2 <= 18
	      x1 >= 0, x2 >= 0
	 alors le fichier contient :
	 2 3
	 2 3 24
	 5 3 30
	 1 3 18
	 4 3
	 Le premier dictionnaire realisable est :
	 x3 = 24 - 2x1 - 3x2
	 x4 = 30 - 5x1 - 3x2
	 x5 = 18 -  x1 - 3x2
	 z  = 0  + 4x1 + 3x2
	 et, apres initialisation : 
	 nbVar = 2
	 nbContraintes = 3
	 tabVarHorsBase est de dimension 2 et contient les entiers 1, 2 (pour les variables de decision x1 et x2)
	 tabVarBase est de dimension 3 et contient les entiers 3, 4, 5 (pour les variables d'ecart x3, x4, x5)
	 Le tableau des constantes est de dimension 3 et contient : 24, 30, 18
	 La matrice coeff contient les coefficients des variables dans le premier dictionnaire (sauf dans z):
	 -2 -3
	 -5 -3
	 -1 -3 
	  Le tableau z contient les coefficients des variables dans z, c'est-à-dire : 4  3
	  z0 est la constante figurant dans z, qui vaut 0.
	  REMARQUE : on dira que la variable x1 est la variable de numero 1, la variable x2 est la variable de numero 2, ...
	  Quand on parlera d'indice, ce sera toujours des indices dans des tableaux et pas des indices de variable.
	 */
	public void lireFichier(File fichier) throws IOException {
		Scanner lecteur ;

		lecteur = new Scanner(fichier);

		lecteur.useLocale(Locale.FRANCE);
		nbVarHorsBase = lecteur.nextInt();
		nbVarBase = lecteur.nextInt();
		allouer();
		for (int i = 1; i <= nbVarBase; i++) {
			tabVarBase[i] = nbVarHorsBase + i;
			for (int j = 1; j <= nbVarHorsBase; j++)  
				dict[i][j] = -lecteur.nextDouble();
			dict[i][0] = lecteur.nextDouble();
		}
		dict[0][0] = 0;
		for (int j = 1; j <= nbVarHorsBase; j++) {
			tabVarHorsBase[j] = j;
			dict[0][j] = lecteur.nextDouble();
		}
		lecteur.close();
	}

	public Dictionnaire(Matrice A, Matrice B, ArrayList<Integer> base, double [] b, double []zDeb, double z0Deb) {
		ArrayList<Integer> colonnes = new ArrayList<Integer>();
		Matrice BInv;
		int nb = base.size();
		double[] cB, y;
		
		for (int x : base) colonnes.add(x - 1); // colonne contient les numeros des variables de base diminues de 1
		BInv = B.getMatriceInverse();
		this.nbVarBase = nb;
		this.nbVarHorsBase = A.getNbColonnes() - nb;
		this.allouer();
		
		double [] constantes = BInv.produitDroit(b);
		for (int i = 1; i <= nb; i++) dict[i][0] = constantes[i - 1];
			
		cB = new double[nb];
		for (int i = 0; i < nb; i++) cB[i] = zDeb[colonnes.get(i)];
		
		dict[0][0] = z0Deb + Matrice.produit(cB, constantes);
		y = BInv.produitGauche(cB);
		
		int indiceBase = 1;
		int indiceHorsBase = 1;
		for (int j = 0; j < A.getNbColonnes(); j++) {
			if (colonnes.contains(j)) {
				tabVarBase[indiceBase] = j + 1;
				indiceBase++;
				continue;
			}
			double[] d;
			double[] a;

			tabVarHorsBase[indiceHorsBase] = j + 1;
			a = A.getColonne(j);
			dict[0][indiceHorsBase] = zDeb[j] - Matrice.produit(y, a);
			d = BInv.produitDroit(a);
			for (int i = 1; i <= nb; i++) dict[i][indiceHorsBase] = -d[i - 1];
			indiceHorsBase++;
		}	
		Simplexe.sortie.afficherDico(this);
	}


	/**
	 * Teste la "presque nullite d'un double" 
	 * @param v le parametre teste.
	 * @return renvoie true si v est considere comme nul, false sinon.
	 */
	public static boolean estNul(double v) {
		return v < Dictionnaire.epsilon && v > -Dictionnaire.epsilon;
	}

	public MethodeEntrante getMethode() {
		return methode;
	}


	private boolean estEcrit = true;
	/**
	 * Permet de choisir l'algorithme de choix de la variable entrante
	 * @param methode la methode choisie pour la variable entrante
	 */
	public void setMethode(MethodeEntrante methode) {
		this.methode = methode;
		if (!estEcrit) Simplexe.sortie.println("On passe a la methode : " + methode + "\n");
		estEcrit = !estEcrit;
	}


	public boolean isBland() {
		return bland;
	}

	/**
	 * Permet de choisir si on utilise la regle de Bland
	 * @param bland si le parametre vaut true, la regle de Bland sera utilisee, sinon elle ne le sera pas
	 */
		boolean debut =true;
		public void setBland(boolean bland) {
		this.bland = bland;
		if (debut) {
			debut = false;
			return;
		}
		if (bland) Simplexe.sortie.println("On utilise le critere de Bland\n");
		else Simplexe.sortie.println("On n'utilise plus le critere de Bland\n");
	}

	public boolean isOptimal() {
		for (int j = 1; j <= this.nbVarHorsBase; j++)
			if (dict[0][j] > 0) return false;
		return true;
	}

	public boolean isBorne() {
		return borne;
	}

	public int getNbHorsBase() {
		return nbVarHorsBase;
	}

	public int getNbBase() {
		return nbVarBase;
	}

	public int[] getVarBase() {
		return tabVarBase;
	}

	public int[] getVarHorsBase() {
		return tabVarHorsBase;
	}

	public double[][] getD() {
		return dict;
	}
	
	public boolean isIncomplet() {
		return incomplet;
	}

	public boolean contient(int num) {
		return estEnBase(num) || estHorsBase(num);
	}
	
	public boolean estEnBase(int num) {
		for (int i = 1; i <= nbVarBase; i++) if (num == tabVarBase[i]) return true;
		return false;
	}
	
	public boolean estHorsBase(int num) {
		for (int j = 1; j <= nbVarHorsBase; j++) if (num == tabVarHorsBase [j]) return true;
		return false;		
	}

}

