package ia.redes.neurais;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author g4l1l30
 */
public class TreinamentoNeural {
    
    private List<Estrutura> dados;
    private List<neuronio> Lco, Lsaida;
    private List<Double> Lerros = new ArrayList<>();

    private double[][] pesosOculta, pesosSaida;
    private int[][] mDesejada;
    private double erro, erroRede = 10, txA;
    private int faOculta, faSaida;
    private int qtIt, qtCo;
    private List<String> rotulos;
    private boolean finished = false;
    Random rand = new Random();

    public TreinamentoNeural(List<Estrutura> dados, double erro, int fao, int fas, double txA, int qtIt, int qtCo) {
        this.dados = dados;
        this.erro = erro;
        this.faOculta = fao;
        this.faSaida = fas;
        this.txA = txA;
        this.qtIt = qtIt;
        this.qtCo = qtCo;
        inicializaMatriz();
    }

    private int getQtSaida() {
        rotulos = new ArrayList();

        for (int i = 0; i < dados.size(); i++)
            if (!rotulos.contains(dados.get(i).getClasse())) 
                rotulos.add(dados.get(i).getClasse());

        return rotulos.size();
    }

    //Inicializa camada oculta e gera pesos aleatorios para a camada
    public void inicializaCO() {
        
        Lco = new ArrayList<>();
        pesosOculta = new double[qtCo][dados.get(0).getAtributos().size()];
        for (int i = 0; i < qtCo; i++)
        {
            Lco.add(new neuronio());
            for (int j = 0; j < dados.get(0).getAtributos().size(); j++)
                pesosOculta[i][j] = rand.nextDouble()*4 - 2.0;
        }
        
    }
    
    //Inicializa camada de saida e gera pesos aleatorios para a camada
    public void inicializaSaida() {
        Lsaida = new ArrayList<>();
        pesosSaida = new double[getQtSaida()][qtCo];
        for (int i = 0; i < getQtSaida(); i++)
        {
            Lsaida.add(new neuronio());
            for (int j = 0; j < qtCo; j++) 
                pesosSaida[i][j] = rand.nextDouble()*4 - 2.0;
        }
    }

    //Iniializa a matriz desejada, camada oculta e de saida
    public void inicializaMatriz() {
        inicializaCO();
        inicializaSaida();
        mDesejada = new int[getQtSaida()][getQtSaida()];
        for (int i = 0; i < getQtSaida(); i++) {
            for (int j = 0; j < getQtSaida(); j++) {
                if (i == j)
                    mDesejada[i][i] = 1;
                else
                    mDesejada[i][j] = 0;
            }
        }
    }

    public double linear(Double net) {
        return net / 10.0;
    }

    public double Dlinear(Double net) {
        return 1.0 / 10.0;
    }

    public double logistica(Double net) {
        return 1.0 / (1.0 + Math.pow(Math.E, -net));
    }

    public double Dlogistica(Double net) {
        return net * (1.0 - net);
    }

    public double hiperbolica(Double net) {
        double d = Math.pow(Math.E, -2.0 * net);
        return (1.0 - d) / (1.0 + d);
    }

    public double Dhiperbolica(Double net) {
        return 1.0 - Math.pow(net, 2);
    }
    
    //1 - Linear
    //2 - Logistica
    //3 - Hiperbolica

    public void treinar() {
        int epocas = 0;
        double som_erro;
        while (erro < erroRede && epocas < qtIt) {
            som_erro = 0;
            for (Estrutura d : dados) {                
                for (int i = 0; i < Lco.size(); i++) // Cálculo do NET
                {
                    double soma = 0;
                    for (int j = 0; j < d.getAtributos().size(); j++)
                        soma += d.getAtributos().get(j) * pesosOculta[i][j];
                        
                    Lco.get(i).setNet(soma);

                    switch (faOculta) {
                    //tg hiperbolica
                        case 3:
                            Lco.get(i).setValor(hiperbolica(Lco.get(i).getNet()));
                            break;
                        case 2:
                            Lco.get(i).setValor(logistica(Lco.get(i).getNet()));
                            break;
                        default:
                            Lco.get(i).setValor(linear(Lco.get(i).getNet()));
                            break;
                    }
                }

                for (int i = 0; i < Lsaida.size(); i++) {
                    double soma = 0;
                    for (int j = 0; j < Lco.size(); j++)
                        soma += Lco.get(j).getValor() * pesosSaida[i][j];
                    
                    Lsaida.get(i).setNet(soma);

                    switch (faSaida) {
                    //tg hiperbolica
                        case 3:
                            Lsaida.get(i).setValor(hiperbolica(Lsaida.get(i).getNet()));
                            break;
                        case 2:                    
                            Lsaida.get(i).setValor(logistica(Lsaida.get(i).getNet()));
                            break;
                        default:
                            Lsaida.get(i).setValor(linear(Lsaida.get(i).getNet()));
                            break;
                    }
                }

                erroRede = 0;
                int index = rotulos.indexOf(d.getClasse());
                for (int i = 0; i < Lsaida.size(); i++) {
                    Lsaida.get(i).setErro((double) mDesejada[index][i] - Lsaida.get(i).getValor());
                    erroRede += Math.pow(Lsaida.get(i).getErro(), 2);

                    switch (faSaida) {
                        case 3:
                            Lsaida.get(i).setGradiente(Lsaida.get(i).getErro() * Dhiperbolica(Lsaida.get(i).getValor()));
                            break;
                        case 2:
                            Lsaida.get(i).setGradiente(Lsaida.get(i).getErro() * Dlogistica(Lsaida.get(i).getValor()));
                            break;
                        default:
                            Lsaida.get(i).setGradiente(Lsaida.get(i).getErro() * Dlinear(Lsaida.get(i).getValor()));
                            break;
                    }
                }

                erroRede /= 2;
                som_erro += erroRede;

                for (int i = 0; i < Lco.size(); i++) {
                    double somErro = 0;
                    for (int j = 0; j < Lsaida.size(); j++) 
                        somErro += Lsaida.get(j).getGradiente() * pesosSaida[j][i];
                    
                    switch (faOculta) {
                        case 3:
                            Lco.get(i).setErro(somErro * Dhiperbolica(Lco.get(i).getValor()));
                            break;
                        case 2:
                            Lco.get(i).setErro(somErro * Dlogistica(Lco.get(i).getValor()));
                            break;
                        default:
                            Lco.get(i).setErro(somErro * Dlinear(Lco.get(i).getValor()));
                            break;
                    }
                }

                //Atualização de pesos da camada de saida
                for (int i = 0; i < Lsaida.size(); i++)
                    for (int j = 0; j < Lco.size(); j++)
                        pesosSaida[i][j] = pesosSaida[i][j] + txA * Lsaida.get(i).getGradiente() * Lco.get(j).getValor();

                //Atualização de pesos da camada oculta
                for (int i = 0; i < Lco.size(); i++)
                    for (int j = 0; j < d.getAtributos().size(); j++) 
                        pesosOculta[i][j] = pesosOculta[i][j] + txA * Lco.get(i).getErro()* d.getAtributos().get(j);
            }
            
            erroRede = som_erro / dados.size();
            System.out.println("Epoca: " + epocas + " Erro:" + erroRede);
            Lerros.add(erroRede);

            if (erroRede < erro)
                break;
            
            epocas++;
        }
        this.finished = true;
    }

    public int[][] teste(List<Estrutura> Lteste) {
        
        int [][] mConfusao = new int[getQtSaida()][getQtSaida()];
        int cA = 0, tot = 0;
        for (Estrutura d : Lteste) {
            for (int i = 0; i < Lco.size(); i++) // Cálculo do NET
            {
                double soma = 0;
                for (int j = 0; j < d.getAtributos().size(); j++)
                    soma += d.getAtributos().get(j) * pesosOculta[i][j];
                
                Lco.get(i).setNet(soma);

                switch (faOculta) {
                //tg hiperbolica
                    case 3:
                        Lco.get(i).setValor(hiperbolica(Lco.get(i).getNet()));
                        break;
                    case 2:
                        Lco.get(i).setValor(logistica(Lco.get(i).getNet()));
                        break;
                    default:
                        Lco.get(i).setValor(linear(Lco.get(i).getNet()));
                        break;
                }
            }

            for (int i = 0; i < Lsaida.size(); i++) {
                double soma = 0;
                for (int j = 0; j < Lco.size(); j++) 
                    soma += Lco.get(j).getValor() * pesosSaida[i][j];
                
                Lsaida.get(i).setNet(soma);

                switch (faSaida) {
                //tg hiperbolica
                    case 3:
                        Lsaida.get(i).setValor(hiperbolica(Lsaida.get(i).getNet()));
                        break;
                    case 2:
                        Lsaida.get(i).setValor(logistica(Lsaida.get(i).getNet()));
                        break;
                    default:
                        Lsaida.get(i).setValor(linear(Lsaida.get(i).getNet()));
                        break;
                }
            }

            int index = 0;
            double maior = Lsaida.get(0).getValor();

            for (int i = 1; i < Lsaida.size(); i++)                
                if (Lsaida.get(i).getValor() > maior) {
                    maior = Lsaida.get(i).getValor();
                    index = i;
                }
            
            int i = 0;
            for (; i < rotulos.size(); i++) {
                if(rotulos.get(i).equals(d.getClasse()))
                    break;
            }
            
            mConfusao[i][index]++;
        }
        return mConfusao;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public List<Double> getErrors() {
        return this.Lerros;
    }

    public List<String> getRotulos()
    {
        return rotulos;
    }
    
    
}
