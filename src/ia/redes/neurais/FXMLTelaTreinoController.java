package ia.redes.neurais;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class FXMLTelaTreinoController implements Initializable {

    @FXML
    private Label lbTrain;
    @FXML
    private TableView tableConfusion;
    @FXML
    private LineChart<String, Double> chartLoss;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private BarChart barChart;

    private TreinamentoNeural net;
    private List<Estrutura> lTeste;
    private Thread thread;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Task<Void> exampleTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            net.treinamento(); // Faz o treinamento da Rede neural
                            geraGrafico(); // gera o grafico 
                            showConfusion(net.teste(lTeste)); //mostra a matriz de confusao
                        }
                    });
                    return null;
                }
            };
            thread = new Thread(exampleTask);
            thread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void geraGrafico() {
        xAxis.setLabel("Épocas");

        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Data Series");

        chartLoss.getData().add(series);

        for (int i = 0; i < net.getErros().size(); i += 50) {
            series.getData().add(new XYChart.Data<String, Double>(i + "", net.getErros().get(i)));
        }
    }

    public void setNet(TreinamentoNeural net) {
        this.net = net;
    }

    public void SetTrest(List<Estrutura> lTeste) {
        this.lTeste = lTeste;
    }

    private void showConfusion(int[][] mConfusion) {
        int total = 0, acertos = 0;
        List<String> split_label = net.getClasses();
        split_label.add(0, " ");

        TableColumn[] tableColumns = new TableColumn[split_label.size()];
        tableConfusion.getColumns().clear();

        for (int i = 0; i < split_label.size(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(split_label.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            tableConfusion.getColumns().addAll(col);
        }

        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        for (int i = 0; i < mConfusion.length; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(split_label.get(i + 1));

            for (int j = 0; j < mConfusion.length; j++) {
                row.add(mConfusion[i][j] + "");
                total += mConfusion[i][j];
                if (i == j) {
                    acertos += mConfusion[i][j];
                }

            }
            data.add(row);
        }

        //grafico em barras
        XYChart.Series serieAcertos = new XYChart.Series();
        serieAcertos.setName("Acertos: " + ((acertos / ((double) total)) * 100.0) + "%");
        serieAcertos.getData().add(new XYChart.Data("Acertos", acertos));

        XYChart.Series serieErros = new XYChart.Series();
        serieErros.setName("Erros: " + (((total - acertos) / (double) total) * 100.0) + "%");
        serieErros.getData().add(new XYChart.Data("Erros", (total - acertos)));

        barChart.getData().addAll(serieAcertos, serieErros);

        tableConfusion.setItems(data);
        tableConfusion.setVisible(true);
        lbTrain.setText("Treinamento finalizado");
        //System.out.println(lbTrain.getText());
    }
}
