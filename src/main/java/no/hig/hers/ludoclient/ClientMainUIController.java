package no.hig.hers.ludoclient;

import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class ClientMainUIController {
    @FXML
    private Button buttonTest;
    
    @FXML
    private TabPane chatTabPane;


    @FXML
    void testCode(ActionEvent event) {
    	Tab newTab = new Tab("random");	
    	chatTabPane.getTabs().add(newTab);
    }
}
