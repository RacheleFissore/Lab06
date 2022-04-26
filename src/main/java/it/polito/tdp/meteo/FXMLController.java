/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	// Qui richiamo la procedura ricorsiva
    	if(boxMese.getValue() != null) {
    		this.model.trovaSequenzaRicorsiva(boxMese.getValue(), new ArrayList<String>(), 0);
        	for (String s : this.model.getElencoCitta()) {
        		this.txtResult.appendText(s + " ");
        	}
    	}
    	else {
    		txtResult.setText("Selezionare un mese");
    	}
    	
    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	txtResult.setText("");
    	if(boxMese.getValue() != null) {
    		String string = model.getUmiditaMedia(boxMese.getValue());
        	txtResult.setText(string);
    	}
    	else {
    		txtResult.setText("Selezionare un mese");
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		
		// Popolo la combobox
		for(int i = 1; i <= 12; i++) {
			this.boxMese.getItems().add(i);
		}
	}
}

