import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Clase que maneja los parametros de ejecucion del programa
 * @author Jose Luis Obiang Ela Nanguang
 * @version 2.0
 */
public class Parametros {

    private final int totalParametros; //Numero de parametros introducidos
    private String[] parametros; //Array de parametros introducidos

    /**
     * Constructor por defecto: Inicializa todas las variables de la clase.
     */
    public Parametros() {
        this.totalParametros = parametros.length;
        this.parametros = new String[totalParametros];
    }

    /**
     * Constructor parametrizado
     * @param parametros, recibe un arreglo de String con los parametros
     */

    public Parametros(String[] parametros) {
        this.parametros = parametros;
        this.totalParametros = parametros.length;
    }

    /**
     * Devuelve el numero de parametros introducidos
     * @return total de parametros
     */
    public int getTotalParametros() {
        return totalParametros;
    }

    /**
     * Devuelve todos los parametros introducidos
     * @return todos los parametros introducidos
     */
    public String[] getParametros() {
        return parametros;
    }


    /**
     * Para mostrar informacion de la clase
     * @return
     */
    @Override
    public String toString() {
        return "Parametros{" +
                "totalParametros=" + totalParametros +
                ", parametros=" + Arrays.toString(parametros) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parametros that = (Parametros) o;
        return totalParametros == that.totalParametros && Arrays.equals(parametros, that.parametros);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(totalParametros);
        result = 31 * result + Arrays.hashCode(parametros);
        return result;
    }

    /**
     * Verifica si se ha introducido parametros
     * @return
     * <ul>
     *     <li>true: si NO se ha introducido parametros </li>
     *     <li>false: si se ha introducido parametros</li>
     * </ul>
     */
    public boolean isEmpty(boolean estadoTraza) {
        if (totalParametros == 0) {
            Procesar.imprimirTrazaConSaltoLinea("No has introducido ningun parametro", estadoTraza);
            return true;
        }
        return false;
    }


    /**
     * Verifica si la cantidad de parametros es menor que 2
     * @return
     * <ul>
     *     <li>true: si la cantidad de parametros es menor que 2</li>
     *     <li>false: si la cantidad de parametros NO es menor que 2</li>
     * </ul>
     */
    public boolean isLessTwoParameters() {
        return totalParametros < 2;
    }

    /**
     * Verifica si la cantidad de parametros es igual a 1
     * @return
     * <ul>
     *     <li>true: si la cantidad de parametros es igual a 1</li>
     *     <li>false: si la cantidad de parametros NO es igual a 1</li>
     * </ul>
     */
    public boolean isOneParameter() {
        return totalParametros == 1;
    }

    /**
     * Verifica si el parametro es -h
     * @param estadoTraza Variable de tipo booleano, recibe el estado de la traza
     * @return
     *<ul>
     *     <li>true: es parametro -h</li>
     *     <li>false: NO es parametro -h</li>
     *</ul>
     *
     */
    public boolean isParameterH(File fileReadme, boolean estadoTraza) throws IOException {
        if (parametros != null && parametros.length > 0) {
            if (parametros[0].equals("-h") || parametros[0].equals("--help")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el parametro es -f
     * @param estadoTraza Variable de tipo booleano, recibe el estado de la traza
     * @return
     *<ul>
     *     <li>true: es parametro -f->Indica la entrada del fichero de configuracion config.txt</li>
     *     <li>false: NO es parametro -f</li>
     *</ul>
     *
     */
    public boolean isParameterF(boolean estadoTraza) {
        if (parametros != null && parametros.length > 0) {
            if (parametros[0].equals("-f") || parametros[0].equals("--fichero")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el parametro es -l
     * @param estadoTraza Variable de tipo booleano, recibe el estado de la traza
     * @return
     *<ul>
     *     <li>true: es parametro -l->Indica la entrada del fichero logs.txt</li>
     *     <li>false: NO es parametro -l</li>
     *</ul>
     *
     */
    public boolean isParameterL(boolean estadoTraza) {
        if (parametros != null && parametros.length > 0) {
            if (parametros[0].equals("-l") || parametros[0].equals("--logs")) {
                return true;
            }
        }
        return false;
    }


}
