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
	private List<Rilevamento> elencoRilevamenti;
	private List<Rilevamento> elencoRilevamentiMeseCitta;
	private double prezzoMigliore = 9999999; // Essendo che devo trovare il minimo parto da un numero molto grande, sappiamo che troveremo
											 // un prezzo sicuramente minore

	public Model() {
		elencoCitta = new ArrayList<String>();
		elencoRilevamentiMeseCitta = new ArrayList<Rilevamento>();
		this.meteoDao = new MeteoDAO();
		citta = this.meteoDao.getLocalita();
		elencoRilevamenti = this.meteoDao.getAllRilevamenti();
		/*citta.add("Milano");
		citta.add("Torino");
		citta.add("Genova");*/
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return meteoDao.getUmiditaMedia(mese);
	}
	
	// of course you can change the String output with what you think works best
	public void trovaSequenzaRicorsiva(int mese, List<String> parziale, int livello) {
		// Livello: è il giorno per cui devo decidere in che città stare, quindi la lista di stringhe conterrà 15 città per i primi 15
		// giorni del mese
		boolean trovato = true;
		
		for(String string : citta) {
			if(!parziale.contains(string)) {
				trovato = false;
			}
		}
		
		if(parziale.size() == 15 && trovato) {
			// TODO: !!!!!! DEVO AGGIUNGERE IL VINCOLO DI AVERE NELLA LISTA TUTTE E 3 LE CITTA !!!!!!!!
			
			// Condizione terminale: il numero di città inserite da visitare è 15 per i primi 15 giorni del mese 
			elencoCitta = new ArrayList<String>(parziale); // Faccio una copia di parziale in elencoCitta che dovrò restituire
			
			// Quando ho trovato una possibile soluzione migliore le assegno il prezzo
			prezzoMigliore = this.calcolaPrezzo(mese, elencoCitta);
			
		} else {
			// Metto i due metodi per il controllo
			for(String c : citta) {
				boolean doRicorsione = true;
				double prezzoParziale = 0;
				int numGCons = this.giorniConsecutiviPerCitta(c, parziale);
				int numGNonCons = this.giorniNonConsecutiviPerCitta(c, parziale);
				if(numGCons < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && numGNonCons < NUMERO_GIORNI_CITTA_MAX 
						&& (livello == 0 || (livello > 0 && parziale.get(livello-1).compareTo(c) == 0)) ) {
					/*
					 * livello > 0 --> Non devo essere nel primo giro se no prima non ho niente in parziale e non entra nell'if
					 * parziale.get(livello-1).compareTo(c) == 0 --> Controllo che la città precedente a quella che sto controllando, cioè 
					 * che voglio aggiungere, sia la stessa.
					 * OR
					 * livello == 0 --> Se sono al primo giro devo sempre inserire la città in parziale 
					 */
					
					// Aggiungo la città che sto scorrendo alla lista perchè devo avercela per almeno 3 giorni consecutivi
					parziale.add(c);
					prezzoParziale = this.calcolaPrezzo(mese, parziale);
					if(prezzoParziale > prezzoMigliore) {
						// La città che ho aggiunto sopra aicuramente mi porta ad una soluzione peggiore
						doRicorsione = false;
					}
				}
				else if(numGCons >= this.NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && numGNonCons < this.NUMERO_GIORNI_CITTA_MAX) {
					// Calcolo prezzo per vedere se è migliore di quello prima e se è peggiore del prezzo migliore la città che sto scorrendo
					// non la aggiunto
					
					parziale.add(c); // quando faccio il controllo del prezzo è da aggiungere
					prezzoParziale = this.calcolaPrezzo(mese, parziale);
					if(prezzoParziale > prezzoMigliore) {
						// La città che ho aggiunto sopra aicuramente mi porta ad una soluzione peggiore
						doRicorsione = false;
					}
				}
				else {
					// Sono nel caso in cui il numero di giorni nella stessa città è maggiore di 6 oppure sto cercando di aggiungere una città
					// alla lista di parziale senza aver messo 3 volte consecutivamente la stessa città in parziale
					 doRicorsione = false;
				}
				
				if(doRicorsione) {
					trovaSequenzaRicorsiva(mese, parziale, livello+1);
					parziale.remove(parziale.size()-1);
				}
			}
		}
	}
	
	public int giorniConsecutiviPerCitta(String citta, List<String> parziale) {
		int cnt = 0;
		// Conto le occorrenze consecutive di quella città nella lista perchè ci devo stare almeno 3 giorni
		
		for(int i = parziale.size()-1; i >= 0; i--) {
			if (i == parziale.size()-1) { //guardo l'elemento in ultima posizione
				cnt ++;
			} else {
				if (parziale.get(i).compareTo(parziale.get(i+1)) == 0) { //guardo se l'elemento del giro corrente è uguale a quello del giro precedente
					cnt ++;
				} else {
					break;
				}
			}
		}
		
		return cnt;
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
	
	public double calcolaPrezzo(int mese, List<String> parziale) {
		// Calcola il prezzo totale fino a quel punto
		double prezzo = 0.0;
		int giorno = 1;
		
		for(String s : parziale) {
			Rilevamento r = this.meteoDao.getRilevamentoLocalitaGiorno(giorno, mese, s);
			prezzo += r.getUmidita();
			if(parziale.size() >= 2 && parziale.get(giorno).compareTo(parziale.get(giorno--)) == 0) {
				prezzo += 100;
			}
			giorno++;
		}
		
		return prezzo;
	}
	
	public List<String> getElencoCitta(){
		return this.elencoCitta;
	}

}
