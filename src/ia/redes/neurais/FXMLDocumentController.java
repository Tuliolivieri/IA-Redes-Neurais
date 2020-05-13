/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.redes.neurais;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
    
    private File file;
    private BufferedReader br;
    private int num_neuronios;
    private String[] neuronios;
    private ObservableList<String[]> dados;
    //private ArrayList<TableColumn> colunas;
    
    @FXML
    private TableView<String[]> tbNeuronios;
    @FXML
    private JFXButton btAvancar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //colunas = new ArrayList<TableColumn>();
        dados = FXCollections.observableArrayList();
        num_neuronios = 0;
    }    

    @FXML
    private void clkAbrirArquivo(ActionEvent event) throws FileNotFoundException, IOException
    {
        FileChooser fileChooser = new FileChooser();

        file = fileChooser.showOpenDialog(btAbrirArquivo.getParent().getScene().getWindow());
        if (file != null) 
        {
            tfArquivo.setText(file.getAbsolutePath());
            
            br = new BufferedReader(new FileReader(file));
            
            String line = br.readLine();
            
            if(line != null)
            {
                neuronios = line.split(",");

                num_neuronios = neuronios.length - 1;
                
                for(int i = 0; i < neuronios.length; i++)
                {
                    TableColumn<String[], String> col = new TableColumn(neuronios[i]);
                    col.setCellValueFactory(new PropertyValueFactory<>(neuronios[i]));
                    tbNeuronios.getColumns().add(col);
                }
                
                line = br.readLine();
                while(line != null)
                {
                    String[] linha = line.split(",");
                    dados.add(linha);
                    tbNeuronios.getItems().add(linha);
                    line = br.readLine();
                }
                /*tbNeuronios.getItems().addAll(dados);
                tbNeuronios.refresh();*/
            }
            System.out.println(neuronios.length);
            
            btAbrirArquivo.setStyle("-fx-border-color: #00ff33");
            tfArquivo.setUnFocusColor(Paint.valueOf("#00ff33"));
        }
    }

    @FXML
    private void clkAvancar(ActionEvent event)
    {
    }
    
}
