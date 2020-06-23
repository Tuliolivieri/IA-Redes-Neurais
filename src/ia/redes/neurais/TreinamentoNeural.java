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
    private List<neuronio> cmdo, Lsaida;
    private List<Double> Lerros = new ArrayList<>();
    private List<String> classes;
    private double[][] pesosOculta, pesosSaida;
    private double erro, erroRede = 10, txA;
    private int[][] mDesejada;
    private int funE, funS, iteracoes, camadaOculta, saida;
    
    Random rand = new Random();

    public TreinamentoNeural(List<Estrutura> dados, double erro, int funE, int funS, double txA, int iteracoes, int camadaOculta, int saida) {
        this.dados = dados;
        this.erro = erro;
        this.funE = funE;
        this.funS = funS;
        this.txA = txA;
        this.iteracoes = iteracoes;
        this.camadaOculta = camadaOculta;
        this.saida = saida;
        inicializaMatriz();
    }
    
    
    public List<Double> getErros() {
        return this.Lerros;
    }

    public List<String> getClasses()
    {
        return classes;
    }

   
    private void carregaClasses() {
        classes = new ArrayList();

        for (int i = 0; i < dados.size(); i++)
            if (!classes.contains(dados.get(i).getClasse())) 
                classes.add(dados.get(i).getClasse());
    }

    //inicializa a camada Oculta e gera pesos aleatorios
    public void inicializaCO() {
        
        cmdo = new ArrayList<>();
        pesosOculta = new double[camadaOculta][dados.get(0).getAtributos().size()];
        for (int i = 0; i < camadaOculta; i++)
        {
            cmdo.add(new neuronio());
            for (int j = 0; j < dados.get(0).getAtributos().size(); j++)
                pesosOculta[i][j] = rand.nextDouble()*4 - 2.0;
        }
        
    }
    
   //inicializa a camada de saida e gera pesos aleatorios
    public void inicializaSaida() {
        Lsaida = new ArrayList<>();
        pesosSaida = new double[saida][camadaOculta];
        for (int i = 0; i < saida; i++)
        {
            Lsaida.add(new neuronio());
            for (int j = 0; j < camadaOculta; j++) 
                pesosSaida[i][j] = rand.nextDouble()*4 - 2.0;
        }
    }

    //inicializa as camadas e gera a matrizz desejada
    public void inicializaMatriz() {
        inicializaCO();
        inicializaSaida();
        carregaClasses();
        mDesejada = new int[saida][saida];
        for (int i = 0; i < saida; i++) {
            for (int j = 0; j < saida; j++) {
                if (i == j)
                    mDesejada[i][i] = 1;
                else
                    mDesejada[i][j] = 0;
            }
        }
    }
    
    

    public void treinamento() {
       
        double som_erro;
        
        for(int e = 0; erro < erroRede && e < iteracoes; e++)
         {
            som_erro = 0;
            for (Estrutura d : dados) 
            {                
                for (int i = 0; i < cmdo.size(); i++) 
                {
                    double soma = 0;
                    for (int j = 0; j < d.getAtributos().size(); j++)
                        soma += d.getAtributos().get(j) * pesosOculta[i][j];
                    cmdo.get(i).setNet(soma);
                    switch (funE) {
                        case 3:
                            cmdo.get(i).setValor(equa(cmdo.get(i).getNet(),5));//hiperbolica
                            break;
                        case 2:
                            cmdo.get(i).setValor(equa(cmdo.get(i).getNet(),3));//logistica
                            break;
                        default:
                            cmdo.get(i).setValor(equa(cmdo.get(i).getNet(),1));//linear
                            break;
                    }
                }

                for (int i = 0; i < Lsaida.size(); i++) {
                    double soma = 0;
                    for (int j = 0; j < cmdo.size(); j++)
                        soma += cmdo.get(j).getValor() * pesosSaida[i][j];
                    Lsaida.get(i).setNet(soma);
                    switch (funS) {
                        case 3:
                            Lsaida.get(i).setValor(equa(Lsaida.get(i).getNet(),5));//hiperbolica
                            break;
                        case 2:                    
                            Lsaida.get(i).setValor(equa(Lsaida.get(i).getNet(),3));//logistica
                            break;
                        default:
                            Lsaida.get(i).setValor(equa(Lsaida.get(i).getNet(),1));//linear
                            break;
                    }
                }

                erroRede = 0;
                int index = classes.indexOf(d.getClasse());
                for (int i = 0; i < Lsaida.size(); i++) {
                    Lsaida.get(i).setErro((double) mDesejada[index][i] - Lsaida.get(i).getValor());
                    erroRede += Math.pow(Lsaida.get(i).getErro(), 2);

                    switch (funS) {
                        case 3:
                            Lsaida.get(i).setGradiente(Lsaida.get(i).getErro() * equa(Lsaida.get(i).getValor(),6));//derivada hiperbolica
                            break;
                        case 2:
                            Lsaida.get(i).setGradiente(Lsaida.get(i).getErro() * equa(Lsaida.get(i).getValor(),4));//derivada logistica
                            break;
                        default:
                            Lsaida.get(i).setGradiente(Lsaida.get(i).getErro() * equa(Lsaida.get(i).getValor(),2));//derivada linear
                            break;
                    }
                }

                erroRede /= 2;
                som_erro += erroRede;

                for (int i = 0; i < cmdo.size(); i++) {
                    double somErro = 0;
                    for (int j = 0; j < Lsaida.size(); j++) 
                        somErro += Lsaida.get(j).getGradiente() * pesosSaida[j][i];
                    
                    switch (funE) {
                        case 3:
                            cmdo.get(i).setErro(somErro * equa(cmdo.get(i).getValor(),6));//derivada hiperbolica
                            break;
                        case 2:
                            cmdo.get(i).setErro(somErro * equa(cmdo.get(i).getValor(),4));//derivada logistica
                            break;
                        default:
                            cmdo.get(i).setErro(somErro * equa(cmdo.get(i).getValor(),2));//derivada linear
                            break;
                    }
                }
                
                //atualiza o peso da camada de saida
                for (int i = 0; i < Lsaida.size(); i++)
                    for (int j = 0; j < cmdo.size(); j++)
                        pesosSaida[i][j] = pesosSaida[i][j] + txA * Lsaida.get(i).getGradiente() * cmdo.get(j).getValor();
                
                //atualiza o peso da camada oculta
                for (int i = 0; i < cmdo.size(); i++)
                    for (int j = 0; j < d.getAtributos().size(); j++) 
                        pesosOculta[i][j] = pesosOculta[i][j] + txA * cmdo.get(i).getErro()* d.getAtributos().get(j);
            }
            
            erroRede = som_erro / dados.size();
            //System.out.println("epoca: " + e + " erro:" + erroRede);
            Lerros.add(erroRede);
            if (erroRede < erro)
                break;
            e++;
        }
    }

    public int[][] teste(List<Estrutura> Lteste) {
        
        int [][] matrizConfusao = new int[saida][saida];
        double soma;
        int w;
        int index;
        for (Estrutura d : Lteste) {
            for (int i = 0; i < cmdo.size(); i++) 
            {
                 soma = 0;
                for (int j = 0; j < d.getAtributos().size(); j++)
                    soma += d.getAtributos().get(j) * pesosOculta[i][j];
                cmdo.get(i).setNet(soma);
                switch (funE) {
                    case 3:
                        cmdo.get(i).setValor(equa(cmdo.get(i).getNet(),5));//hiperbolica
                        break;
                    case 2:
                        cmdo.get(i).setValor(equa(cmdo.get(i).getNet(),3));//logistica
                        break;
                    default:
                        cmdo.get(i).setValor(equa(cmdo.get(i).getNet(),1));//linear
                        break;
                }
            }

            for (int i = 0; i < Lsaida.size(); i++) {
                 soma = 0;
                for (int j = 0; j < cmdo.size(); j++) 
                    soma += cmdo.get(j).getValor() * pesosSaida[i][j];
                Lsaida.get(i).setNet(soma);
                switch (funS) {
                    case 3:
                        Lsaida.get(i).setValor(equa(Lsaida.get(i).getNet(),5));//hiperbolica
                        break;
                    case 2:
                        Lsaida.get(i).setValor(equa(Lsaida.get(i).getNet(),3));//logistica
                        break;
                    default:
                        Lsaida.get(i).setValor(equa(Lsaida.get(i).getNet(),1));//linear
                        break;
                }
            }

            index = 0;
            double maior = Lsaida.get(0).getValor();
            for (int i = 1; i < Lsaida.size(); i++)                
                if (Lsaida.get(i).getValor() > maior) {
                    maior = Lsaida.get(i).getValor();
                    index = i;
                }
            
            w = 0;
            for (; w < saida; w++) {
                if(classes.get(w).equals(d.getClasse()))
                    break;
            }
            matrizConfusao[w][index]++;
        }
        return matrizConfusao;
    }
    
    //escolhe quais funcoes ira usar
    public double equa(Double net,int qual){
        
         switch (qual) {
                    //linear
                    case 1:
                         return net / 10.0;
                    //derivada da linear
                    case 2:
                         return 1.0 / 10.0;
                    //logistica    
                    case 3:
                         return 1.0 / (1.0 + Math.pow(Math.E, -net));
                    //derivada da logistica    
                    case 4:
                         return net * (1.0 - net);
                    //hiperbolica
                    case 5:
                         double d = Math.pow(Math.E, -2.0 * net);
                         return (1.0 - d) / (1.0 + d);
                    //derivada da hiperbolica    
                    case 6:
                         return 1.0 - Math.pow(net, 2);     
                }
         return 0;
         
        }
    
  
    
}
