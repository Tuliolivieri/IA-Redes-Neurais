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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author tulio
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private JFXButton btAbrirArquivo;
    @FXML
    private JFXTextField tfArquivo;
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
    private JFXTextField tfN;
    @FXML
    private JFXTextField tfNumIteracoes;
    @FXML
    private JFXTextField tfArquivoTeste;
    @FXML
    private RadioButton rbEntradaLinear;
    @FXML
    private ToggleGroup transferFunctionEntrada;
    @FXML
    private RadioButton rbEntradaLogistica;
    @FXML
    private RadioButton rbEntradaHiperbolica;
    @FXML
    private RadioButton rbSaidaLinear;
    @FXML
    private ToggleGroup transferFunctionSaida;
    @FXML
    private RadioButton rbSaidaLogistica;
    @FXML
    private RadioButton rbSaidaHiperbolica;

    //VARIAVEIS
    private File fileTreino;
    private File fileTeste;
    private BufferedReader br;
    private BufferedReader br2;
    private TableColumn[] colTabela;
    private int num_neuronios, saida, oculta, entrada,funE,funS;
    private double mediaGeometrica;
    private double[] vetmax;
    private double[] vetmin;
    private String[] neuronios;
    private ObservableList<ObservableList> dadostr;
    private List<Estrutura> dados;
    private List<Estrutura> teste;
    private ArrayList<String> saidas;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dadostr = FXCollections.observableArrayList();
        num_neuronios = 0;
        saidas = new ArrayList<>();
        mediaGeometrica = 0;
        btAvancar.setDisable(true);
        dados = new ArrayList<>();
        teste = new ArrayList<>();
                
    }

    @FXML
    private void clkAbrirArquivo(ActionEvent event) throws FileNotFoundException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir arquivo de Treino");
        fileTreino = fileChooser.showOpenDialog(null);
        fileChooser.setInitialDirectory(new File(fileTreino.getParent()));
        
        
        if (fileTreino != null) {
            tfArquivo.setText(fileTreino.getAbsolutePath());
            br = new BufferedReader(new FileReader(fileTreino));
            br2 = new BufferedReader(new FileReader(fileTreino));

            String line = br.readLine();
            if (line != null) {
                neuronios = line.split(",");
                num_neuronios = neuronios.length - 1;
                colTabela = new TableColumn[neuronios.length];
                for (int i = 0; i < neuronios.length; i++) {
                    final int j = i;
                    TableColumn col = new TableColumn(neuronios[i]);
                    col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                    tbNeuronios.getColumns().addAll(col);
                }

                vetmin = new double[num_neuronios];
                vetmax = new double[num_neuronios];
                for (int i = 0; i < num_neuronios; i++) {
                    vetmin[i] = Double.MAX_VALUE;
                    vetmax[i] = Double.MIN_VALUE;
                }

                line = br2.readLine();//Descarta a 1ª linha de informaçoes com nomes
                line = br2.readLine();
                while (line != null) {
                    String[] linha = line.split(",");
                    Estrutura dt = new Estrutura();
                    for (int i = 0; i < num_neuronios; i++) {
                        dt.setAtributos(Double.parseDouble(linha[i]));
                        
                        if (vetmin[i] > Double.parseDouble(linha[i])) {
                            vetmin[i] = Double.parseDouble(linha[i]);
                        } else if (vetmax[i] < Double.parseDouble(linha[i])) {
                            vetmax[i] = Double.parseDouble(linha[i]);
                        }
                    }
                    dt.setClasse(linha[linha.length-1]);
                    dados.add(dt);
                    line = br2.readLine();
                }
                
                for (Estrutura d : dados) {
                for (int i = 0; i < d.getAtributos().size(); i++) {
                    Double newValor = ((double) d.getAtributos().get(i) - vetmin[i]) / (vetmax[i] - vetmin[i]);

                    d.getAtributos().set(i, newValor);
                }
            }

                ObservableList<String> row;
                line = br.readLine();
                while (line != null) {
                    String[] linha = line.split(",");

                    for (int i = 0; i < num_neuronios; i++) {
                        double n = Double.parseDouble(linha[i]);
                        linha[i] = ((n - vetmin[i]) / (vetmax[i] - vetmin[i])) + "";
                        
                    }
                    row = FXCollections.observableArrayList();
                    row.addAll(linha);
                    dadostr.add(row);
                    if (!saidas.contains(linha[num_neuronios])) {
                        saidas.add(linha[num_neuronios]);
                    }
                    line = br.readLine();
                }
                tbNeuronios.getItems().addAll(dadostr);
            }

            mediaGeometrica = Math.sqrt(saidas.size() * num_neuronios);
            
            entrada = num_neuronios;
            saida = saidas.size();
            oculta = (int)mediaGeometrica;
            
            tfCamadaEntrada.setText(entrada + "");
            tfCamadaSaida.setText(saida + "");
            tfCamadaOculta.setText(oculta + "");
            tfNumIteracoes.setText("2000");
            tfValorErro.setText("0.00001");
            tfN.setText("0.2");
            rbEntradaLinear.setSelected(true);
            rbSaidaLinear.setSelected(true);
            tfArquivo.setUnFocusColor(Paint.valueOf("#00ff33"));

            
            fileChooser.setTitle("Abrir arquivo de Teste");
            fileTeste = fileChooser.showOpenDialog(null);
            fileChooser.setInitialDirectory(new File(fileTeste.getParent()));
            if (fileTeste != null) {
                btAbrirArquivo.setStyle("-fx-border-color: #00ff33");
                tfArquivoTeste.setText(fileTeste.getAbsolutePath());
                tfArquivoTeste.setUnFocusColor(Paint.valueOf("#00ff33"));
                btAvancar.setDisable(false);

                ObservableList<String> row;
                br2 = new BufferedReader(new FileReader(fileTeste));
                line = br2.readLine();
                line = br2.readLine();
                while (line != null) {
                    String[] linha = line.split(",");
                    Estrutura te = new Estrutura();
                    for (int i = 0; i < num_neuronios; i++) {
                        double n = Double.parseDouble(linha[i]);
                        linha[i] = ((n - vetmin[i]) / (vetmax[i] - vetmin[i])) + "";
                        te.setAtributos(Double.parseDouble(linha[i]));
                    }
                    te.setClasse(linha[linha.length-1]);
                    teste.add(te);
                    line = br2.readLine();
                }
            }
        }
        System.out.println("");
    }
    
    private void funEntrada()
    {
        if(rbEntradaLinear.isSelected())
            funE = 1;
        else
            if(rbEntradaLogistica.isSelected())
                funE = 2;
            else
                funE = 3;
    }
    
    private void funSadia()
    {
        if(rbSaidaLinear.isSelected())
            funS = 1;
        else
            if(rbSaidaLogistica.isSelected())
                funS = 2;
            else
                funS = 3;
    }

    @FXML
    private void clkAvancar(ActionEvent event) {
        funEntrada();
        funSadia();
        TreinamentoNeural rede = new TreinamentoNeural(dados, 
                                            Double.parseDouble(tfValorErro.getText()), 
                                            funE,
                                            funS, 
                                            Double.parseDouble(tfN.getText()), 
                                            Integer.parseInt(tfNumIteracoes.getText()), 
                                            Integer.parseInt(tfCamadaOculta.getText()),
                                            saida);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLTelaTreino.fxml"));
            Parent root = (Parent) loader.load();
            FXMLTelaTreinoController t = loader.getController();
            t.setNet(rede);
            t.SetTrest(teste);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

 }


