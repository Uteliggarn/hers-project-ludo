package no.hig.hers.ludoclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import no.hig.hers.ludoclient.Main;

public class ClientTempUIController {

    @FXML
    private Button buttonSpill;

    @FXML
    private Button buttonChat;

    @FXML
    private Button buttonMain;

    @FXML
    void goToChat(ActionEvent event) {

    }

    @FXML
    void goToSpill(ActionEvent event) {

    }
    
    
    @FXML
    void goToMain(ActionEvent event) {
    	Main.changeScene(Main.mainScene);
    }

}
