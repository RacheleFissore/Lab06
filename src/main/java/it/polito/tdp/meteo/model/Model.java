package it.polito.tdp.meteo.model;

import java.util.*;

import it.polito.tdp.meteo.DAO.MeteoDAO;
import javafx.util.converter.ShortStringConverter;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteoDao;
	private List<String> elencoCitta; // Conterrà l'ordine delle città visitate nei 15 giorni
	private List<String> citta; // Contiene le 3 città visitabili
	private double prezzoMigliore = 9999999; // Essendo che devo trovare il minimo parto da un numero molto grande, sappiamo che troveremo
											 // un prezzo sicuramente minore

	public Model() {
		elencoCitta = new ArrayList<String>();
		this.meteoDao = new MeteoDAO();
		citta = this.meteoDao.getLocalita();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return meteoDao.getUmiditaMedia(mese);
	}
	
	public void trovaSequenzaRicorsiva(int mese, List<String> parziale, int livello) {
		// Livello: è il giorno per cui devo decidere in che città stare, quindi la lista di stringhe conterrà 15 città per i primi 15
		// giorni del mese
		boolean trovato = true;
		for(String string : citta) {
			if(!parziale.contains(string)) {
				trovato = false;
			}
		}
		
		// Condizione terminale: il numero di città inserite da visitare è 15 per i primi 15 giorni del mese 
		if(parziale.size() == NUMERO_GIORNI_TOTALI && trovato) {
			// Calcolo il prezzo associato alla lista parziale, che è una delle possibili soluzioni
			double prezzoParz = this.calcolaPrezzo(mese, new ArrayList<String>(parziale));
			
			if(prezzoParz < prezzoMigliore) { // Al primo giro sicuramente il prezzo parziale sarà minore di quello migliore, quindi vado ad
											  // assegnare al prezzo migliore quello che ho trovato. Dal secondo giro quando avrò una nuova 
											  // soluzione confronto il prezzo associato a questa soluzione con quello associato alla soluzione
											  // migliore.
				prezzoMigliore = prezzoParz;				
				elencoCitta = new ArrayList<String>(parziale); // Faccio una copia di parziale in elencoCitta che dovrò restituire
			}
		} else {
			for(String c : citta) {
				// Conto quante sono le occorrenze già inserite per una città nella lista parziale
				int numGNonCons = this.giorniNonConsecutiviPerCitta(c, parziale);
				if(controlla(c, parziale) && numGNonCons < NUMERO_GIORNI_CITTA_MAX) {
					// Entra qui dentro aggiungendo la nuova città nel caso in cui controlla() restituisce true, ossia sono stati rispettati
					// tutti i vincoli e che non abbia già inserito 6 volte quella città nella lista
					parziale.add(c);
					trovaSequenzaRicorsiva(mese, parziale, livello+1);
					parziale.remove(parziale.size()-1); // backtracking
				}
			}
			
		}
	}
	
	public int giorniNonConsecutiviPerCitta(String citta, List<String> parziale) {
		int cnt = 0;	
		
		// Conto le occorrenze consecutive di quella città nella lista perchè non ci devo stare più di 6 giorni anche non consecutivi
		for(String s : parziale) {
			if(s.compareTo(citta) == 0) {
				cnt++;					
			}
		}
		return cnt;
	}
	
	
	public boolean controlla(String citta, List<String> parziale) {
		parziale.add(citta); // Suppongo di controllare cosa succede aggiungendo la città alla lista per provare se vengono rispettati tutti
							 // i controlli nel caso in cui venga inserita. Se tutti i controlli sono rispettati allora la aggiungerò 
							 // definitivamente
		boolean ok = false;
		if(parziale.size() == 0) // Se non ho ancora elementi lo aggiungo
			ok = true;
		else if(parziale.size() == 1) { // Confronto primo con la città che voglio inserire
			if(parziale.get(0).compareTo(citta) == 0)
				ok = true;
		}
		else if(parziale.size() == 2) { // Confronto primo e secondo elemento
			if(parziale.get(1).compareTo(parziale.get(0)) == 0)
				ok = true;
		}
		else { // Confronto quando ho 3 o più elementi
			int dim = parziale.size();
			// Controllo se la lista è piena, in questo caso allora devo controllare se gli ultimi 3 elementi sono uguali tra loro
			if(parziale.size() == NUMERO_GIORNI_TOTALI) {
				if(parziale.get(NUMERO_GIORNI_TOTALI-1).compareTo(parziale.get(NUMERO_GIORNI_TOTALI-2)) == 0 && 
						parziale.get(NUMERO_GIORNI_TOTALI-2).compareTo(parziale.get(NUMERO_GIORNI_TOTALI-3)) == 0) {
					ok = true;	
				}
			}
			else {
				// Controllo se gli ultimi due elementi sono diversi tra loro, in questo caso vuol dire che sto cambiando città e allora
				// controllo se almeno 3 degli elementi precedenti sono uguali tra loro
				if(parziale.size() >= 4 && parziale.get(dim-1).compareTo(parziale.get(dim-2)) != 0) { 
					if(parziale.get(dim-2).compareTo(parziale.get(dim-3)) == 0 &&
							parziale.get(dim-3).compareTo(parziale.get(dim-4)) == 0) {
						ok = true;
					}
				}
				// Caso in cui ho 3 città uguali e poi 2 città diverse, controllo se l'ultima e la terz'ultima sono diverse, in questo caso 
				// vuol dire che devo ancora inserire una città uguale alle ultime due, ma prima di farlo controllo se almeno 3 degli elementi 
				// precedenti a queste ultime due città sono uguali tra loro
				else if(parziale.size() >= 5 && parziale.get(dim-1).compareTo(parziale.get(dim-3)) != 0) {
					if(parziale.get(dim-3).compareTo(parziale.get(dim-4)) == 0 &&
							parziale.get(dim-4).compareTo(parziale.get(dim-5)) == 0) {
						ok = true;
					}
				}
				// Controllo se gli ultimi 3 elementi sono uguali tra di loro
				else if(parziale.get(dim-1).compareTo(parziale.get(dim-2)) == 0 &&
						parziale.get(dim-2).compareTo(parziale.get(dim-3)) == 0) {
					ok = true;
				}
			}
			
		}
		
		parziale.remove(parziale.size()-1); // Tolgo da parziale l'elemento che ho provato ad inserire perchè non è detto che vada bene, 
											// in base a cosa mi restituisce questa funzione lo aggiungerò definitivamente o no nella
											// procedura ricorsiva 
		return ok;
	}

	public double calcolaPrezzo(int mese, List<String> parziale) {
		double prezzo = 0.0;
		int giorno = 1;
		
		for(String s : parziale) {
			// Mi prendo il rilevamento associato alla città che sto scorrendo dalla lista, al giorno e al mese
			Rilevamento r = this.meteoDao.getRilevamentoLocalitaGiorno(giorno, mese, s);
			if(r != null) { // Il database ha dei buchi, quindi controllo che esista un rilevamento per il giorno che sto considerando,
							// in caso affermativo allora sommo al prezzo l'umidità, altrimenti vado avanti con il giorno e controllo il
							// rilevamento del giorno successivo
				prezzo += r.getUmidita();
				if(parziale.size() >= 2 && giorno < NUMERO_GIORNI_TOTALI && parziale.get(giorno).compareTo(parziale.get(giorno-1)) != 0) {
					// Controllo se sto cambiando città devo sommare +100 al prezzo. Inoltre il controllo giorno < NUMERO_GIORNI_TOTALI
					// serve per evitare di fare questo controllo nel caso in cui giorno = 15 perchè altrimenti vado fuori dalla posizione
					// della lista e poi tanto gli ultimi 3 giorni devono essere uguali tra di loro quindi sicuramente non dovrò sommare
					// questi 100
					prezzo += COST;
				}
			}
			
			giorno++;
		}
		
		return prezzo;
	}
	
	public List<String> getElencoCitta(){		
		return this.elencoCitta;
	}
}
