/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.redes.neurais;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Paint;

/** 
 *
 * @author tulio
 */
public class FXMLDocumentController implements Initializable
{

    @FXML
    private ToggleGroup transferFunction;
    @FXML
    private JFXButton btAbrirArquivo;
    @FXML
    private JFXTextField tfArquivo;
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }    

    @FXML
    private void clkAbrirArquivo(ActionEvent event)
    {
        btAbrirArquivo.setStyle("-fx-border-color: #00ff33");
        tfArquivo.setUnFocusColor(Paint.valueOf("#00ff33"));
    }
    
}
