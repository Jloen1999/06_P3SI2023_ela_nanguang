import java.io.IOException;
import java.util.Scanner;

public class P3_si2023 {
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        // Comprobar parametros de entrada
        Parametros parametros = new Parametros(args);
        Instancias instancias = new Instancias();

        EjecutarInstruccionesConfig runConfig = new EjecutarInstruccionesConfig(instancias, parametros);
        MostrarAyuda showH = new MostrarAyuda(instancias, parametros);

        showH.showH();

        if (parametros.isLessTwoParameters()) { // Si-> < 2 parametros

            if (parametros.isOneParameter()) { // Si->1 Parametro

                showH.showH();

            } else {

                parametros.isEmpty(runConfig.getInstancias().isEstadoTraza()); // Sin parametros

            }

        } else { // >= 2 parametros

            if (parametros.isParameterF(runConfig.getInstancias().isEstadoTraza())) { // Parametro 1: -f

                runConfig.runConfig();

            } else {
                Procesar.imprimirTrazaConSaltoLinea("Error, Parametro incorrecto", runConfig.getInstancias().isEstadoTraza());
            }
        }


        Procesar.imprimirTrazaConSaltoLinea("___________________________________________\n" +
                "Hasta una proxima ejecucion\n" +
                "___________________________________________\n", runConfig.getInstancias().isEstadoTraza());
        System.out.println("FIN DEL PROGRAMA POR AUSENCIA DEL FICHERO DE CONFIGURACION");

    }


}
