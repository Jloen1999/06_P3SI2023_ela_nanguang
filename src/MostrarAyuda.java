import java.io.IOException;

public class MostrarAyuda {

    private Instancias instancias;
    private Parametros parametros;

    public MostrarAyuda(Instancias instancias, Parametros parametros){
        this.instancias = instancias;
        this.parametros = parametros;
    }

    public MostrarAyuda(){
        this.instancias = new Instancias();
        this.parametros = new Parametros();
    }

    public void showH() throws IOException {
        if (parametros.isParameterH(instancias.getFileReadme(), instancias.isEstadoTraza())) { // Si Unico parametro: -h

            Procesar.leerFichero(instancias.getFileReadme(), instancias.isEstadoTraza()); // Lectura fichero de ayuda

        } else if (parametros.isParameterF(instancias.isEstadoTraza())) { // Si Unico parametro: -f

            Procesar.imprimirTrazaConSaltoLinea("Falta el archivo de configuracion", instancias.isEstadoTraza());

        } else if (parametros.isParameterL(instancias.isEstadoTraza())) { // Si Unico parametro: -l

            Procesar.imprimirTrazaConSaltoLinea("LOGS:", instancias.isEstadoTraza());

            Procesar.printLogs(instancias.getFileLogs(), instancias.isEstadoTraza()); // Imprimir fichero Logs

        } else { // Parametro invalido: Permmitidos->-h, -f, -l

            Procesar.imprimirTrazaConSaltoLinea("Error, parametro incorrecto", instancias.isEstadoTraza());
            Procesar.imprimirTrazaConSaltoLinea("La sintaxis correcta es: P3_SI.2023 [-f config.txt] | [-h]", instancias.isEstadoTraza());

        }
    }
}
