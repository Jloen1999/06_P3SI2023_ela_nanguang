import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interfaz que se encarga de las operaciones de procesado de ficheros
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 2.0
 */

public interface Procesar {

    /**
     * Lee un fichero y muestra su contenido
     *
     * @param fichero     recibe el fichero a leer
     * @param estadoTraza recibe el estado de la traza
     * @throws IOException
     */
    public static int leerFichero(File fichero, boolean estadoTraza) throws IOException {
        String linea = "";
        FileReader readme = null;
        BufferedReader lectorReadme = null;
        int totalLineas = 0;
        try {

            readme = new FileReader(fichero);
            lectorReadme = new BufferedReader(readme);

            while ((linea = lectorReadme.readLine()) != null) {
                if (fichero.getName().equalsIgnoreCase("logs.txt")) {
                    if (linea.toLowerCase().contains("texto en claro formateado:")) {
                        Procesar.imprimirTrazaConSaltoLinea("\t\t\t\t" + (totalLineas + 1), estadoTraza);
                        Procesar.imprimirTrazaConSaltoLinea("Texto en claro Formateado: " + linea.substring(linea.indexOf(":") + 1), estadoTraza);
                        totalLineas++;
                    } else if (linea.toLowerCase().contains("criptograma:")) {
                        Procesar.imprimirTrazaSinSaltoLinea("CRIPTOGRAMA: " + linea.substring(linea.indexOf(":") + 1) + "\n", estadoTraza);
                    }
                } else {
                    Procesar.imprimirTrazaConSaltoLinea(linea, estadoTraza);
                }

            }
        } catch (FileNotFoundException e) {
            Procesar.imprimirTrazaSinSaltoLinea("No existe el fichero: " + fichero.getPath(), estadoTraza);
        } finally {
            if (lectorReadme != null) lectorReadme.close();
            if (readme != null) readme.close();
        }

        return totalLineas;
    }

    /**
     * Comprueba si existe un fichero
     *
     * @param fichero Objeto de tipo File, recibe el fichero a comprobar
     * @return <ul>
     * <li>tru: si existe el fichero</li>
     * <li>false: si no existe el fichero</li>
     * </ul>
     */
    public static boolean comprobarFichero(File fichero, String compare, boolean estadoTraza) {
        boolean fileExist = false;
        if (fichero != null) {
            if (!fichero.exists()) { // Comprobar si existe el fichero
                fileExist = false;
            } else if (fichero.exists() && fichero.length() < 0) { //Si existe y no contiene nada

                Procesar.imprimirTrazaConSaltoLinea("El fichero " + fichero.getPath() + " existe pero esta vacio", estadoTraza);
                fileExist = true;

            } else {

                fileExist = true; // Si existe el fichero

            }
        }

        return fileExist;
    }

    /**
     * Verifica si una lnea esta vaca.
     *
     * @param linea Variable de tipo cadena, recibe la linea del contenido de un fichero
     * @return booleano
     * <ul>
     *     <li>true si la linea esta vacia</li>
     *     <li>false si la linea no esta vacia</li>
     * </ul>
     */
    public static boolean isEmptyLine(String linea) {
        return linea.trim().isEmpty();
    }


    /**
     * Verifica si el primer caracter de la linea es un comando.
     *
     * @param linea Variable de tipo cadena, recibe la linea del contenido de un fichero
     * @return <ul>
     * <li>true: si el primer caracter de la linea es un comando, bandera o comentario</li>
     * <li>false: si el primer caracter de la linea no es un comando, bandera o comentario</li>
     * </ul>
     */
    public static char isCommandFlagComment(String linea, boolean estadoTraza) {
        char cfc = ' ';
        switch (linea.charAt(0)) {
            case '&':
                cfc = '&';
                //Procesar.imprimirTrazaConSaltoLinea("Comando", estadoTraza);
                break;
            case '@':
                cfc = '@';
                //Procesar.imprimirTrazaConSaltoLinea("Bandera", estadoTraza);
                break;
            case '#':
                cfc = '#';
                break;
            default:
                Procesar.imprimirTrazaConSaltoLinea("----------No es un Comando, Bandera o Comentario-----", estadoTraza);
        }

        return cfc;
    }


    /**
     * Devuelve un fichero de entrada o salida.
     *
     * @param linea       Variable de tipo cadena, recibe la linea del contenido de un fichero
     * @param compare     Variable de tipo cadena, recibe el texto a comparar para saber si se trata de un fichero de entrada o de salida
     * @param pathInit    Variable de tipo cadena, recibe el path
     * @param estadoTraza Variable de tipo booleano, recibe el estado de la traza
     * @return fichero
     */

    public static File getFile(String linea, String compare, String pathInit, boolean estadoTraza) {

        int ultimoIndice = 0;
        String path = "";
        File fichero = null;
        if (compare.equalsIgnoreCase("ficheroentrada")) {
            path = pathInit + getWordForPos(linea, 2);
        } else if (compare.equalsIgnoreCase("ficherosalida")) {
            path = pathInit + getWordForPos(linea, 2);
        } else if (compare.equalsIgnoreCase("fichero_clave")) {
            path = pathInit + getWordForPos(linea, 2);
        }

        fichero = new File(path); //Obtener fichero de E/S, formateado o fichero clave

        return fichero;

    }

    /**
     * Comprueba si una linea contiene un texto especfico.
     *
     * @param linea   Variable de tipo cadena, recibe la linea del contenido de un fichero
     * @param compare Variable de tipo cadena, recibe el texto a comparar
     * @param cf      Variable de tipo caracter, recibe el caracter comando o bandera
     * @return Devuelve
     * <ul>
     *     <li style="color: green">true: contiene la cadena pasada como parametro </li>
     *     <li style="color: red">false: NO contiene la cadena pasada como parametro </li>
     * </ul>
     */
    public static boolean containComparator(String linea, String compare, char cf) {
        linea = linea.toLowerCase();
        String regex = cf + "\\s+" + compare.toLowerCase();  // \s+ busca uno o más espacios

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(linea);

        return matcher.find();
    }


    /**
     * Obtiene una palabra por posicion en una cadena
     *
     * @param sentence Variable de tipo cadena, recibe una cadena
     * @param pos      Variable de tipo entero, recibe la posicion de la cadena a obtener
     * @return
     */
    public static String getWordForPos(String sentence, int pos) {
        String[] words = sentence.trim().split("\\s+");
        try{
            return words[pos];
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("No ha existe la clave de Usuario");
        }
        return "";
    }


    /**
     * Imprime mensajes en la consola con salto de linea segun este el estado de la traza.
     * <ul>estadoTraza
     *     <li>true: Imprime mensajes por consola</li>
     *     <li>false: NO imprime mensajes por consola</li>
     * </ul>
     *
     * @param salida      Variable de tipo cadena, recibe el mensaje a mostrar por consola
     * @param estadoTraza Variable de tipo boolean, recibe el estado de la traza
     */
    public static void imprimirTrazaConSaltoLinea(String salida, boolean estadoTraza) {
        if (estadoTraza) {
            System.out.println(salida);
        }
    }

    /**
     * Imprime mensajes en la consola sin salto de linea segun este el estado de la traza.
     * <ul>estadoTraza
     *     <li>true: Imprime mensajes por consola</li>
     *     <li>false: NO imprime mensajes por consola</li>
     * </ul>
     *
     * @param salida      Variable de tipo cadena, recibe el mensaje a mostrar por consola
     * @param estadoTraza Variable de tipo boolean, recibe el estado de la traza
     */
    public static void imprimirTrazaSinSaltoLinea(String salida, boolean estadoTraza) {
        if (estadoTraza) {
            System.out.print(salida);
        }
    }

    public static void printLogs(File fileLogs, boolean estadoTraza) {

        if (!comprobarFichero(fileLogs, "", estadoTraza)) { // Si no existe fichero Logs

            try {

                if (fileLogs.createNewFile()) { // Crear fichero Logs

                    imprimirTrazaConSaltoLinea("FICHERO LOGS CREADO", estadoTraza);

                } else { // Fichero Logs no creado

                    imprimirTrazaConSaltoLinea("ERROR en la creacion del fichero de logs", estadoTraza);

                }

            } catch (IOException e) {

            }

        } else { // Si existe fichero Logs

            try {
                leerFichero(fileLogs, estadoTraza); // Lectura del fichero logs
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static String printBytes(byte[] d) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d.length; i++) {
            sb.append(String.format("%x", d[i]));
            if (i != (d.length - 1)) {
                sb.append(":");
            }
        }

        return sb.toString();
    }

    public static String obtenerExtension(String nombreArchivo) {
        // Encuentra la posición del último punto en el nombre del archivo
        int indiceUltimoPunto = nombreArchivo.lastIndexOf('.');

        // Verifica si se encontró un punto y si no está al final del nombre del archivo
        if (indiceUltimoPunto > 0 && indiceUltimoPunto < nombreArchivo.length() - 1) {
            // Extrae la subcadena después del último punto
            return nombreArchivo.substring(indiceUltimoPunto);
        } else {
            // No se encontró un punto o está al final, por lo que no hay extensión
            return "";
        }
    }

    public static byte[] cifrar(String texto, SecretKey clv, int[] vecInit) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException {
        // Cifrador
        Cipher cifrador = Cipher.getInstance("AES/ECB/NOPadding");
        cifrador.init(Cipher.ENCRYPT_MODE, clv);

        String[] bloques = new String[texto.length() / 16];
        byte[] v = ajustarNumerosABytes(vecInit);
        byte[] data = new byte[bloques.length * 16];
        int dataIndex = 0;

        // Hay que asegurarse que el texto tiene al menos 16 bytes
        if (texto.length() < 16) {
            return data;
        }

        for (int i = 0; i < bloques.length; i++) {
            int inicio = i * 16;
            int fin = inicio + 16;
            bloques[i] = texto.substring(inicio, fin);
            System.out.println("Texto bloque " + (i + 1) + ": " + bloques[i]);

            byte[] t = bloques[i].getBytes();
            System.out.printf("Bloque " + (i + 1) + ": ");
            printBytes(t);
            System.out.printf("----> V.I.: ");
            printBytes(v);
            byte[] xor = xorBytes(t, v);
            System.out.printf("----> XOR: ");
            printBytes(xor);
            byte[] cripto = null;
            try {
                cripto = cifrador.doFinal(xor);
            } catch (IllegalBlockSizeException e) {
                System.out.println("--> BLOQUE ILEGAL: El size introducido no es correcto.");
            }

            System.out.printf("----> Criptograma: ");
            printBytes(cripto);

            // Copiar los bytes cifrados al array data manualmente
            for (int j = 0; j < 16; j++) {
                data[dataIndex] = cripto[j];
                dataIndex++;
            }
            v = cripto;
        }
        return data;
    }

    public static String descifrar(byte[] data, SecretKey clv, int[] vecInit) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException {
        // Cifrador
        Cipher cifrador = Cipher.getInstance("AES/ECB/NoPadding");
        cifrador.init(Cipher.DECRYPT_MODE, clv);

        byte[] v = ajustarNumerosABytes(vecInit);

        StringBuilder textoDescifrado = new StringBuilder();

        for (int i = 0; i < data.length; i += 16) {
            byte[] bloqueCifrado = new byte[16];
            System.arraycopy(data, i, bloqueCifrado, 0, 16);
            System.out.printf("Bloque Cifrado " + (i / 16 + 1) + ": ");
            printBytes(bloqueCifrado);
            byte[] xor = null;
            try {
                xor = cifrador.doFinal(bloqueCifrado);
            } catch (IllegalBlockSizeException e) {
                System.out.println("--> BLOQUE ILEGAL: El size introducido no es correcto.");
            }
            System.out.printf("----> XOR: ");
            printBytes(xor);
            byte[] textoPlano = xorBytes(xor, v);
            System.out.printf("----> Texto Plano: ");
            printBytes(textoPlano);
            v = bloqueCifrado;

            textoDescifrado.append(new String(textoPlano));
        }

        return textoDescifrado.toString();
    }

    public static byte[] xorBytes(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];

        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }

        return result;
    }

    public static byte[] ajustarNumerosABytes(int[] numeros) {
        byte[] iv = new byte[16]; // Vector de inicializacion de 16 bytes

        for (int i = 0; i < 16; i++) {
            // Ajusta cada numero al rango de un byte (0-255)
            iv[i] = (byte) (numeros[i] % 256);
        }

        return iv;
    }

    public static byte[] cifrarAES_CBC(String textoPlano, SecretKey secretKey, IvParameterSpec iv)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(textoPlano.getBytes());
    }

    public static String descifrarAES_CBC(byte[] textoCifrado, SecretKey secretKey, IvParameterSpec iv)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] textoDescifrado = cipher.doFinal(textoCifrado);
        return new String(textoDescifrado);
    }

}
