package ia.redes.neurais;

/**
 *
 * @author g4l1l30
 */
public class neuronio {
    
    private double erro, valor, net, gradiente;

    public neuronio() {
        this.erro = 0;
        this.valor = 0;
        this.net = 0;
        this.gradiente = 0;
    }

    public double getErro() {
        return erro;
    }

    public void setErro(double erro) {
        this.erro = erro;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getGradiente() {
        return gradiente;
    }

    public void setGradiente(double gradiente) {
        this.gradiente = gradiente;
    }
    
    
    
    
    
}
