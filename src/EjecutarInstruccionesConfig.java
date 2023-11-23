import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EjecutarInstruccionesConfig {

    private Instancias instancias;
    private Parametros parametros;
    private String linea = ""; // Variable que almacerna cada una de las lineas del contenido de un fichero


    public EjecutarInstruccionesConfig(Instancias instancias, Parametros parametros) {
        this.instancias = instancias;
        this.parametros = parametros;
        this.linea = "";
    }

    public EjecutarInstruccionesConfig() {
        this.instancias = new Instancias();
        this.parametros = new Parametros();
        this.linea = "";
    }

    public void runConfig() throws IOException {
        // Fichero de configuracion
        File fileConfig = null;
        fileConfig = new File(instancias.getPathInit() + parametros.getParametros()[1]);
        if (Procesar.comprobarFichero(fileConfig, "config", instancias.isEstadoTraza())) { // Si existe el fichero de configuracion

            FileReader fileConfigReader = null;
            BufferedReader configBR = null;

            Procesar.printLogs(instancias.getFileLogs(), instancias.isEstadoTraza()); // Imprimir fichero Logs

            try {

                fileConfigReader = new FileReader(fileConfig);
                configBR = new BufferedReader(fileConfigReader);

                //Leer fichero de configuracion
                while ((linea = configBR.readLine()) != null) { // Leer el archivo

                    if (!Procesar.isEmptyLine(linea)) { // Linea vacía

                        if (Procesar.isCommandFlagComment(linea, instancias.isEstadoTraza()) == '#') { // Comentario

                            Procesar.imprimirTrazaConSaltoLinea("Comentario", instancias.isEstadoTraza());

                        } else if (Procesar.isCommandFlagComment(linea, instancias.isEstadoTraza()) == '&') { // Si Comando

                            if (linea.substring(1, 2).equals(" ")) { // Si comando valido, el & debe ir seguido de un espacio

                                if (Procesar.containComparator(linea, instancias.getPalabrasClave()[0], '&')) { // Genera_Clave <tamano> AES <cadena>

                                    int tamano = Integer.parseInt(Procesar.getWordForPos(linea, 2));
                                    Procesar.imprimirTrazaConSaltoLinea("---->Tamaño: " + tamano, instancias.isEstadoTraza());

                                    String cifrado = Procesar.getWordForPos(linea, 3);
                                    if (cifrado.equals("AES")) {

                                        Procesar.imprimirTrazaConSaltoLinea("Cifrado AES...", instancias.isEstadoTraza());

                                    }

                                    String claveUsuario = Procesar.getWordForPos(linea, 4);
                                    Procesar.imprimirTrazaConSaltoLinea("Clave de Usuario: " + claveUsuario, instancias.isEstadoTraza());

                                    // Comprobar tamaño
                                    if (tamano == 128 || tamano == 192 || tamano == 256) {

                                        Procesar.imprimirTrazaConSaltoLinea("------------- |Generar clave AES aleatoria de tamaño| -------------" + tamano, instancias.isEstadoTraza());

                                        // Instanciar objeto KeyGenerator y SecretKey(clave)
                                        KeyGenerator generadorAES = null;

                                        if (claveUsuario.length() >= 16) {  // Con password de usuario

                                            Procesar.imprimirTrazaConSaltoLinea("------------->Generar clave AES con password de usuario: " + claveUsuario, instancias.isEstadoTraza());
                                            byte[] claveUsuarioByte = null;
                                            try {

                                                claveUsuarioByte = claveUsuario.getBytes("UTF-8");

                                            } catch (UnsupportedEncodingException ex) {

                                                Logger.getLogger(P3_si2023.class.getName()).log(Level.SEVERE, null, ex);

                                            }

                                            // Hacemos un resumen "SHA-1" del texto de clave del usuario
                                            MessageDigest sha = null;
                                            try {

                                                sha = MessageDigest.getInstance("SHA-256");
                                                claveUsuarioByte = sha.digest(claveUsuarioByte);
                                                claveUsuarioByte = Arrays.copyOf(claveUsuarioByte, tamano / 8);
                                                instancias.setClave(new SecretKeySpec(claveUsuarioByte, "AES")); // Genera clave AES

                                            } catch (NoSuchAlgorithmException e) {
                                                Procesar.imprimirTrazaConSaltoLinea("-------->ERROR, No se ha podido encontrar el algoritmo SHA-256 " + e.getMessage(), instancias.isEstadoTraza());
                                            }

                                        } else {

                                            Procesar.imprimirTrazaConSaltoLinea("\t-----------------------Generar clave AES aleatoria de tamaño " + tamano + "\t-----------------------", instancias.isEstadoTraza());
                                            try {

                                                generadorAES = KeyGenerator.getInstance("AES");
                                                generadorAES.init((short) tamano); // clave de 128, 192 o 256 bits
                                                instancias.setClave(generadorAES.generateKey());


                                            } catch (NoSuchAlgorithmException e) {
                                                Procesar.imprimirTrazaConSaltoLinea("ERROR, No se ha podido encontrar el algoritmo AES " + e.getMessage(), instancias.isEstadoTraza());
                                            }


                                        }

                                        // Almacenar o cargar clave AES en un fichero clave
                                        try {

                                            if (Procesar.comprobarFichero(instancias.getFileKey(), instancias.getPalabrasClave()[2], instancias.isEstadoTraza())) {
                                                FileOutputStream fosKey = new FileOutputStream(instancias.getFileKey());
                                                ObjectOutputStream oosKey = new ObjectOutputStream(fosKey);
                                                oosKey.writeObject(instancias.getClave());
                                                fosKey.close();
                                                Procesar.imprimirTrazaSinSaltoLinea("\t----->La clave " + Procesar.printBytes(instancias.getClave().getEncoded()) + " se ha almacenado correctamente en el fichero " + instancias.getFileKey().getName(), instancias.isEstadoTraza());
                                            } else {
                                                Procesar.imprimirTrazaConSaltoLinea("\nError, no existe el fichero clave ", instancias.isEstadoTraza());
                                            }

                                        } catch (IOException e) {
                                            System.out.println("Ha ocurrido un error al intentar almacenar la clave en el archivo " + instancias.getFileKey().getName());
                                        }


                                    } else {
                                        Procesar.imprimirTrazaConSaltoLinea("ERROR, El tamaño debe ser 128, 192 o 256", instancias.isEstadoTraza());
                                        System.out.println("FIN DEL PROGRAMA POR AUSENCIA DEL FICHERO DE CONFIGURACION");
                                    }


                                } else if (Procesar.containComparator(linea, instancias.getPalabrasClave()[1], '&')) { // Carga_Clave AES

                                    Procesar.imprimirTrazaConSaltoLinea("-------->Recuperando clave " + Procesar.getWordForPos(linea, 2) + " del fichero clave " + instancias.getFileKey() + " ...", instancias.isEstadoTraza());

                                    FileInputStream fisKey = null;
                                    ObjectInputStream oisKey = null;

                                    try {

                                        fisKey = new FileInputStream(instancias.getFileKey());
                                        oisKey = new ObjectInputStream(fisKey);
                                        instancias.setClave((SecretKey) oisKey.readObject());

                                        System.out.println("\t---->Clave recuperada: " + Procesar.printBytes(instancias.getClave().getEncoded()) + " del fichero clave " + instancias.getFileKey() + " ...");

                                    } catch (FileNotFoundException f) {

                                        Procesar.imprimirTrazaSinSaltoLinea("----No se ha encontrado el fichero clave " + instancias.getFileKey(), instancias.isEstadoTraza());

                                    } catch (ClassNotFoundException e) {

                                        Procesar.imprimirTrazaConSaltoLinea("----ERROR, No se ha podido cargado correctamente la clave", instancias.isEstadoTraza());

                                    } finally {
                                        try {
                                            if (oisKey != null) {
                                                oisKey.close();
                                            }
                                            if (fisKey != null) {
                                                fisKey.close();
                                            }
                                        } catch (IOException e) {
                                            Procesar.imprimirTrazaConSaltoLinea("----ERROR, No se ha podido cerrar el fichero clave " + instancias.getFileKey(), instancias.isEstadoTraza());
                                        }
                                    }


                                } else if (Procesar.containComparator(linea, instancias.getPalabrasClave()[2], '&')) { // Fichero clave

                                    Procesar.imprimirTrazaConSaltoLinea("---------------- |Estableciendo fichero clave...| ----------------", instancias.isEstadoTraza());
                                    instancias.setFileKey(Procesar.getFile(linea, instancias.getPalabrasClave()[2], instancias.getPathInit(), instancias.isEstadoTraza())); // Establecer fichero clave

                                    if (!Procesar.comprobarFichero(instancias.getFileKey(), instancias.getPalabrasClave()[2], instancias.isEstadoTraza())) { // Si no existe el fichero clave

                                        Procesar.imprimirTrazaConSaltoLinea("------->FICHERO CLAVE INEXISTENTE: ", instancias.isEstadoTraza());

                                        if (instancias.getFileKey().createNewFile()) {
                                            Procesar.imprimirTrazaConSaltoLinea("----->Fichero clave " + instancias.getFileKey() + " creado correctamente", instancias.isEstadoTraza());
                                        } else {
                                            Procesar.imprimirTrazaConSaltoLinea("------Error al crear el fichero clave " + instancias.getFileKey(), instancias.isEstadoTraza());
                                        }

                                    }else{

                                        Procesar.imprimirTrazaConSaltoLinea("------->FICHERO CLAVE ESTABLECIDO: "+instancias.getFileKey().getName(), instancias.isEstadoTraza());

                                    }

                                } else if (Procesar.containComparator(linea, instancias.getPalabrasClave()[3], '&')) { // Fichero entrada

                                    Procesar.imprimirTrazaConSaltoLinea("Estableciendo fichero de entrada", instancias.isEstadoTraza());
                                    instancias.setInputFile(Procesar.getFile(linea, instancias.getPalabrasClave()[3], instancias.getPathInit(), instancias.isEstadoTraza())); // Establecer fichero entrada

                                    if (!Procesar.comprobarFichero(instancias.getInputFile(), instancias.getPalabrasClave()[3], instancias.isEstadoTraza())) { // Si no existe el fichero de entrada

                                        Procesar.imprimirTrazaConSaltoLinea("-----NO se ha encontrado el fichero de entrada " + instancias.getInputFile(), instancias.isEstadoTraza());

                                    } else {

                                        instancias.setExtension(Procesar.obtenerExtension(Procesar.getWordForPos(linea, 2)).toLowerCase());
                                        Procesar.imprimirTrazaConSaltoLinea("Extension del fichero de entrada: " + instancias.getExtension(), instancias.isEstadoTraza());
                                        if (instancias.getExtension().equals(".txt")) {

                                            FileReader frInputFile = null;
                                            BufferedReader brInputFile = null;
                                            try {

                                                frInputFile = new FileReader(instancias.getInputFile());
                                                brInputFile = new BufferedReader(frInputFile);
                                                while ((linea = brInputFile.readLine()) != null) {
                                                    System.out.println("---------->Texto en claro: " + Procesar.printBytes(linea.getBytes()));
                                                    instancias.setTextoEnClaro(linea);
                                                }
                                            } catch (FileNotFoundException f) {
                                                Procesar.imprimirTrazaConSaltoLinea("---No se ha encontrado el fichero de entrada " + instancias.getInputFile(), instancias.isEstadoTraza());
                                            } finally {
                                                if (brInputFile != null) {
                                                    try {
                                                        brInputFile.close();
                                                    } catch (IOException c) {
                                                        Procesar.imprimirTrazaConSaltoLinea("----ERROR, No se ha podido cerrar el fichero de entrada " + instancias.getInputFile(), instancias.isEstadoTraza());
                                                    }
                                                }
                                                if (frInputFile != null) {
                                                    try {
                                                        frInputFile.close();
                                                    } catch (IOException e) {
                                                        Procesar.imprimirTrazaConSaltoLinea("-----ERROR, No se ha podido cerrar el fichero de entrada " + instancias.getInputFile(), instancias.isEstadoTraza());
                                                    }
                                                }
                                            }


                                        } else if (instancias.getExtension().equals(".dat")) {

                                            Procesar.imprimirTrazaConSaltoLinea("Extension del fichero de salida: " + instancias.getExtension(), instancias.isEstadoTraza());
                                            try (FileInputStream fisInputFile = new FileInputStream(instancias.getInputFile())) {

                                                instancias.setCriptograma(new byte[fisInputFile.available()]);
                                                fisInputFile.read(instancias.getCriptograma());

                                                Procesar.imprimirTrazaConSaltoLinea("--->Fichero de entrada: " + Procesar.printBytes(instancias.getCriptograma()), instancias.isEstadoTraza());

                                            } catch (FileNotFoundException e) {
                                                Procesar.imprimirTrazaConSaltoLinea("----No se ha encontrado el fichero de entrada " + instancias.getInputFile(), instancias.isEstadoTraza());
                                            }

                                        }

                                    }

                                } else if (Procesar.containComparator(linea, instancias.getPalabrasClave()[4], '&')) { //Fichero salida

                                    Procesar.imprimirTrazaConSaltoLinea("Estableciendo fichero de salida ...", instancias.isEstadoTraza());
                                    instancias.setOutputFile(Procesar.getFile(linea, instancias.getPalabrasClave()[4], instancias.getPathInit(), instancias.isEstadoTraza())); // Obtener fichero salida
                                    if (!Procesar.comprobarFichero(instancias.getOutputFile(), instancias.getPalabrasClave()[4], instancias.isEstadoTraza())) { // Si no existe el fichero salida

                                        if (instancias.getOutputFile().createNewFile()) {
                                            Procesar.imprimirTrazaConSaltoLinea("----Fichero de salida " + instancias.getOutputFile() + " creado correctamente", instancias.isEstadoTraza());
                                        } else {
                                            Procesar.imprimirTrazaConSaltoLinea("----Error al crear el fichero de salida " + instancias.getOutputFile(), instancias.isEstadoTraza());
                                        }

                                    }

                                } else if (Procesar.containComparator(linea, instancias.getPalabrasClave()[5], '&')) { // AES

                                    instancias.setTipoCifrado(Procesar.getWordForPos(linea, 1));
                                    Procesar.imprimirTrazaConSaltoLinea("Cifrado|Descifrado " + instancias.getTipoCifrado(), instancias.isEstadoTraza());

                                    // Inicialización Cipher para cifrar o descifrar.
                                    Cipher cifrador = null;

                                    if (Procesar.getWordForPos(linea, 2).equalsIgnoreCase("ConRelleno")) {

                                        Procesar.imprimirTrazaConSaltoLinea("\t\t\t\t\t\t |ConRelleno|", instancias.isEstadoTraza());
                                        instancias.setRelleno(true);
                                        try {
                                            cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                        } catch (NoSuchAlgorithmException e) {
                                            throw new RuntimeException(e);
                                        } catch (NoSuchPaddingException e) {
                                            Procesar.imprimirTrazaConSaltoLinea("----No se ha hecho correctamente el relleno " + e.getMessage(), instancias.isEstadoTraza());
                                        }


                                    } else {

                                        Procesar.imprimirTrazaConSaltoLinea("\t\t\t\t\t\t |SinRelleno|", instancias.isEstadoTraza());
                                        instancias.setRelleno(false);

                                        try {
                                            cifrador = Cipher.getInstance("AES/ECB/NOPadding");
                                        } catch (NoSuchAlgorithmException e) {
                                            throw new RuntimeException(e);
                                        } catch (NoSuchPaddingException e) {
                                            throw new RuntimeException(e);
                                        }


                                    }

                                    if (instancias.getCommandCodDes().equals("on")) {

                                        Procesar.imprimirTrazaConSaltoLinea("---------------------- |Cifrando Texto en claro...| ----------------------", instancias.isEstadoTraza());
                                        // Inicializar cifrador en modo CIFRADO

                                        try {

                                            cifrador.init(Cipher.ENCRYPT_MODE, instancias.getClave());
                                            byte[] bufferCifrado = null;
                                            try {

                                                bufferCifrado = cifrador.doFinal(instancias.getTextoEnClaro().getBytes());
                                                String criptogramaString = Base64.getEncoder().encodeToString(bufferCifrado);
                                                System.out.println("\t-------->Criptograma: " + criptogramaString);

                                                FileOutputStream ficheroSalida;
                                                ficheroSalida = new FileOutputStream(instancias.getOutputFile());
                                                ficheroSalida.write(bufferCifrado);
                                                ficheroSalida.close();

                                            } catch (IllegalBlockSizeException e) {
                                                throw new RuntimeException(e);
                                            } catch (BadPaddingException e) {
                                                Procesar.imprimirTrazaConSaltoLinea("------El fichero de entrada no tiene el relleno " + e.getMessage(), instancias.isEstadoTraza());
                                            }

                                        } catch (InvalidKeyException e) {
                                            throw new RuntimeException(e);
                                        }


                                    } else if (instancias.getCommandCodDes().equals("off")) {

                                        Procesar.imprimirTrazaConSaltoLinea("---------------------- |Descifrando Criptograma...| ----------------------", instancias.isEstadoTraza());

                                        // Inicializar cifrador en modo DESCIFRADO
                                        try {

                                            cifrador.init(Cipher.DECRYPT_MODE, instancias.getClave());
                                            byte[] bufferDescifrado = null;
                                            try {

                                                bufferDescifrado = cifrador.doFinal(instancias.getCriptograma());
                                                String descifrado = new String(bufferDescifrado);
                                                System.out.println("\t--------->Texto en claro recuperado: " + descifrado);

                                                FileOutputStream fos = new FileOutputStream(instancias.getOutputFile());
                                                fos.write(descifrado.getBytes());
                                                fos.close();
                                            } catch (IllegalBlockSizeException | BadPaddingException e) {
                                                throw new RuntimeException(e);
                                            }

                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }


                                    }

                                } else if (Procesar.containComparator(linea, instancias.getPalabrasClave()[6], '&')) { // CBC

                                    instancias.setTipoCifrado(Procesar.getWordForPos(linea, 1));

                                    String[] words = linea.trim().split("\\s+");
                                    int[] vectorInit = new int[words.length - 2];

                                    for (int i = 2; i < words.length; i++) {
                                        vectorInit[i - 2] = Integer.parseInt(words[i]);
                                    }

                                    Procesar.imprimirTrazaConSaltoLinea("-------->Bytes del vector de inicialización de la clave: " + Arrays.toString(vectorInit), instancias.isEstadoTraza());

                                    // Vector de inicialización (IV) de 16 bytes
                                    if (vectorInit.length != 16) {

                                        Procesar.imprimirTrazaConSaltoLinea("------Error de sintaxis de comando", instancias.isEstadoTraza());

                                    } else {

                                        if (instancias.getCommandCodDes().equals("on")) {
                                            // Cifrado
                                            try {
                                                Procesar.imprimirTrazaConSaltoLinea("\n\t-------------- |Cifrado CBC| -------------------", instancias.isEstadoTraza());
                                                instancias.setCriptograma(Procesar.cifrar(instancias.getTextoEnClaro(), instancias.getClave(), vectorInit));
                                                System.out.println("\n\t\t\tCriptograma----->Cifrado CBC: " + Procesar.printBytes(instancias.getCriptograma()));

                                                FileOutputStream fs = new FileOutputStream(instancias.getOutputFile());
                                                fs.write(instancias.getCriptograma());
                                                fs.close();

                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }


                                        } else if (instancias.getCommandCodDes().equals("off")) {
                                            // Descifrado
                                            try {
                                                Procesar.imprimirTrazaConSaltoLinea("\n\t-------------- |Descifrado CBC| -------------------", instancias.isEstadoTraza());
                                                instancias.setTextoEnClaro(Procesar.descifrar(instancias.getCriptograma(), instancias.getClave(), vectorInit));
                                                System.out.println("\n\t\t\tTexto en claro recuperado---> Cifrado CBC: " + instancias.getTextoEnClaro());

                                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(instancias.getOutputFile()), "UTF-8"));
                                                bw.write(instancias.getTextoEnClaro());
                                                bw.close();

                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }

                                        }

                                    }


                                }
                            } else {

                                Procesar.imprimirTrazaConSaltoLinea("------Error de sintaxis de comando", instancias.isEstadoTraza());

                            }

                        } else if (Procesar.isCommandFlagComment(linea, instancias.isEstadoTraza()) == '@') {

                            String s = Procesar.getWordForPos(linea, 2).toLowerCase();
                            if (Procesar.getWordForPos(linea, 1).equalsIgnoreCase("codifica")) {

                                instancias.setCommandCodDes(s);

                            } else if (Procesar.getWordForPos(linea, 1).equalsIgnoreCase("traza")) {

                                if (s.equalsIgnoreCase("off")) {
                                    instancias.setEstadoTraza(false);
                                } else if (s.equalsIgnoreCase("on")) {
                                    instancias.setEstadoTraza(true);
                                }
                            }


                        }

                    } else {

                        Procesar.imprimirTrazaConSaltoLinea("----Linea vacia", instancias.isEstadoTraza());

                    }
                }
            } catch (IOException e) {
                Procesar.imprimirTrazaConSaltoLinea("-----Error al leer el archivo: " + e.getMessage(), instancias.isEstadoTraza());
            } finally {
                try {
                    if (configBR != null) {
                        configBR.close();
                    }
                    if (fileConfigReader != null) {
                        fileConfigReader.close();

                    }
                } catch (IOException e) {
                    Procesar.imprimirTrazaConSaltoLinea("-----Error al cerrar el archivo: " + e.getMessage(), instancias.isEstadoTraza());
                } finally {
                    if (configBR != null) {
                        configBR.close();
                    }
                    if (fileConfigReader != null) {
                        fileConfigReader.close();

                    }
                }
            }

        }
    }

    public Instancias getInstancias() {
        return instancias;
    }
}