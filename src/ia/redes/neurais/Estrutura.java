package ia.redes.neurais;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author g4l1l30
 */
public class Estrutura {
    private List<Double> atributos = new ArrayList<>();
    private String classe;

    public Estrutura(List<Double> atributos, String classe) {
        this.atributos = atributos;
        this.classe = classe;
    }

    public Estrutura() {
    }

    public List<Double> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<Double> atributos) {
        this.atributos = atributos;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public void setAtributos(Double d)
    {
        atributos.add(d);
    }
    
}
