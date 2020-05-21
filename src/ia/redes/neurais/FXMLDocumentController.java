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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

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
    
    private File fileTreino;
    private File fileTeste;
    private BufferedReader br;
    private BufferedReader br2;
    private int num_neuronios;
    private String[] neuronios;
    private ObservableList<ObservableList> dados;
    private TableColumn[] colTabela;
    private ArrayList<String> saidas;
    private double mediaGeometrica;
    
    @FXML
    private TableView<ObservableList> tbNeuronios;
    @FXML
    private JFXButton btAvancar;
    @FXML
    private JFXTextField tfCamadaEntrada;
    @FXML
    private JFXTextField tfCamadaSaida;
    @FXML
    private JFXTextField tfCamadaOculta;
    @FXML
    private JFXTextField tfValorErro;
    @FXML
    private RadioButton rbLinear;
    @FXML
    private RadioButton rbLogistica;
    @FXML
    private RadioButton rbHiperbolica;
    @FXML
    private JFXTextField tfN;
    @FXML
    private JFXTextField tfNumIteracoes;
    @FXML
    private JFXTextField tfArquivoTeste;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //colunas = new ArrayList<TableColumn>();
        dados = FXCollections.observableArrayList();
        num_neuronios = 0;
        saidas = new ArrayList<String>();
        mediaGeometrica = 0;
        
        btAvancar.setDisable(true);
    }    

    @FXML
    private void clkAbrirArquivo(ActionEvent event) throws FileNotFoundException, IOException
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir arquivo de Treino");
        fileTreino = fileChooser.showOpenDialog(btAbrirArquivo.getParent().getScene().getWindow());
        if (fileTreino != null) 
        {
            tfArquivo.setText(fileTreino.getAbsolutePath());
            
            br = new BufferedReader(new FileReader(fileTreino));
            
            br2 = new BufferedReader(new FileReader(fileTreino));
            
            String line = br.readLine();
            
            if(line != null)
            {
                neuronios = line.split(",");

                num_neuronios = neuronios.length - 1;
                
                colTabela = new TableColumn[neuronios.length];
                for(int i = 0; i < neuronios.length; i++)
                {
                    final int j = i;
                    TableColumn col = new TableColumn(neuronios[i]);
                    col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                        @Override
                        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                             
                    return new SimpleStringProperty(param.getValue().get(j).toString());} 
                    });
                    tbNeuronios.getColumns().addAll(col);
                }
                
                double[] vetmin = new double[num_neuronios];
                double[] vetmax = new double[num_neuronios];
                
                for(int i = 0; i < num_neuronios; i++)
                    vetmin[i] = Double.MAX_VALUE;
                
                line = br2.readLine();
                line = br2.readLine();
                while(line != null)
                {
                    String[] linha = line.split(",");
                    for(int i = 0; i < num_neuronios; i++)
                    {
                        if(vetmin[i] > Double.parseDouble(linha[i]))
                            vetmin[i] = Double.parseDouble(linha[i]);
                        else if(vetmax[i] < Double.parseDouble(linha[i]))
                            vetmax[i] = Double.parseDouble(linha[i]);
                    }
                    line = br2.readLine();
                }
                
                for(int i = 0; i < num_neuronios; i++)
                {
                    System.out.println("Min: " + vetmin[i] + ", Max: " + vetmax[i]);
                }
                
                ObservableList<String> row;
                
                line = br.readLine();
                while(line != null)
                {
                    String[] linha = line.split(",");
                    
                    for(int i = 0; i < num_neuronios; i++)
                    {
                        double n = Double.parseDouble(linha[i]);
                        linha[i] = ((n - vetmin[i])/(vetmax[i] - vetmin[i])) + "";
                    }
                    
                    row = FXCollections.observableArrayList();
                    row.addAll(linha);
                    //dados.add(linha);
                    dados.add(row);
                    
                    if(!saidas.contains(linha[num_neuronios]))
                        saidas.add(linha[num_neuronios]);
                    
                    line = br.readLine();
                }
                tbNeuronios.getItems().addAll(dados);
            }
            
            mediaGeometrica = Math.sqrt(saidas.size() * num_neuronios);
            System.out.println("MG: " + mediaGeometrica);
            System.out.println("Num. Dados: " + dados.size());
            
            tfCamadaEntrada.setText(num_neuronios + "");
            tfCamadaSaida.setText(saidas.size() + "");
            tfCamadaOculta.setText((int)mediaGeometrica + "");
            tfNumIteracoes.setText("2000");
            tfValorErro.setText("0.00001");
            tfN.setText("0.2");
            rbLinear.setSelected(true);
            tfArquivo.setUnFocusColor(Paint.valueOf("#00ff33"));
            
            fileChooser.setTitle("Abrir arquivo de Teste");
            fileTeste = fileChooser.showOpenDialog(btAbrirArquivo.getParent().getScene().getWindow());
            if(fileTreino != null)
            {
                btAbrirArquivo.setStyle("-fx-border-color: #00ff33");
                tfArquivoTeste.setText(fileTeste.getAbsolutePath());
                tfArquivoTeste.setUnFocusColor(Paint.valueOf("#00ff33"));
                btAvancar.setDisable(false);
            }
        }
    }

    @FXML
    private void clkAvancar(ActionEvent event)
    {
    }
    
}
