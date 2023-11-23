import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;

public class Instancias {
    private String pathInit;
    private String commandCodDes;
    private boolean relleno;
    private SecretKey clave;
    private byte[] criptograma;
    private String textoEnClaro;
    private String extension;
    private String tipoCifrado;


    // Ficheros
    private File fileReadme;
    private File fileLogs;
    private File fileKey;
    private File inputFile;
    private File outputFile;

    boolean estadoTraza; //Traza de activación: true->traza ON; false->traza OFF

    private String[] palabrasClave; // Palabras clave del fichero de configuracion

    private IvParameterSpec ivParameterSpec;

    public Instancias() {
        this.pathInit = "";
        this.commandCodDes = "on";
        this.relleno = true;
        this.clave = null;
        this.criptograma = null;
        this.textoEnClaro = "";
        this.extension = "";
        this.tipoCifrado = "AES";
        this.fileReadme = new File(pathInit + "README.txt"); //Fichero de ayuda
        this.fileLogs = new File(pathInit + "LOGS.txt"); //Fichero de logs
        this.fileKey = null; // Fichero clave
        this.inputFile = null; // Fichero entrada
        this.outputFile = null; // Fichero salida
        this.estadoTraza = true; //Traza de activación: true->traza ON; false->traza OFF
        this.palabrasClave = new String[]{"Genera_Clave", "Carga_Clave", "fichero_clave", "ficheroentrada", "ficherosalida", "AES", "CBC"}; // Palabras clave del fichero de configuracion
        this.ivParameterSpec = null;
    }

    public String getPathInit() {
        return pathInit;
    }

    public void setPathInit(String pathInit) {
        this.pathInit = pathInit;
    }

    public String getCommandCodDes() {
        return commandCodDes;
    }

    public void setCommandCodDes(String commandCodDes) {
        this.commandCodDes = commandCodDes;
    }

    public boolean isRelleno() {
        return relleno;
    }

    public void setRelleno(boolean relleno) {
        this.relleno = relleno;
    }

    public SecretKey getClave() {
        return clave;
    }

    public void setClave(SecretKey clave) {
        this.clave = clave;
    }

    public byte[] getCriptograma() {
        return criptograma;
    }

    public void setCriptograma(byte[] criptograma) {
        this.criptograma = criptograma;
    }

    public String getTextoEnClaro() {
        return textoEnClaro;
    }

    public void setTextoEnClaro(String textoEnClaro) {
        this.textoEnClaro = textoEnClaro;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTipoCifrado() {
        return tipoCifrado;
    }

    public void setTipoCifrado(String tipoCifrado) {
        this.tipoCifrado = tipoCifrado;
    }

    public File getFileReadme() {
        return fileReadme;
    }

    public void setFileReadme(File fileReadme) {
        this.fileReadme = fileReadme;
    }

    public File getFileLogs() {
        return fileLogs;
    }

    public void setFileLogs(File fileLogs) {
        this.fileLogs = fileLogs;
    }

    public File getFileKey() {
        return fileKey;
    }

    public void setFileKey(File fileKey) {
        this.fileKey = fileKey;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isEstadoTraza() {
        return estadoTraza;
    }

    public void setEstadoTraza(boolean estadoTraza) {
        this.estadoTraza = estadoTraza;
    }

    public String[] getPalabrasClave() {
        return palabrasClave;
    }

    public void setPalabrasClave(String[] palabrasClave) {
        this.palabrasClave = palabrasClave;
    }

    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
        this.ivParameterSpec = ivParameterSpec;
    }
}
