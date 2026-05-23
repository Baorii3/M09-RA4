import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 9999;
    public static final String DIR_ARRIBADA = System.getProperty("java.io.tmpdir");

    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;

    public void connectar() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Connectant a -> " + HOST + ":" + PORT);
        sortida = new ObjectOutputStream(socket.getOutputStream());
        sortida.flush();
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public void rebreFitxers() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String rutaFitxer = sc.nextLine();
            if (rutaFitxer == null || rutaFitxer.isBlank()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            if ("sortir".equalsIgnoreCase(rutaFitxer)) {
                System.out.println("Sortint...");
                return;
            }

            sortida.writeObject(rutaFitxer);
            sortida.flush();
            System.out.println("Nom del fitxer a guardar: " + rutaFitxer);

            Object obj = entrada.readObject();
            if (!(obj instanceof byte[])) {
                System.out.println("No s'ha rebut cap fitxer.");
                return;
            }

            byte[] contingut = (byte[]) obj;
            File directori = new File(DIR_ARRIBADA);
            directori.mkdirs();
            File desti = new File(directori, new File(rutaFitxer).getName());

            try (FileOutputStream fos = new FileOutputStream(desti)) {
                fos.write(contingut);
            }

            System.out.println("Fitxer rebut i guardat com: " + desti.getPath());
        } catch (Exception e) {
            System.out.println("Error rebent fitxer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void tancarConnexio() {
        try {
            if (entrada != null) entrada.close();
            if (sortida != null) sortida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws Exception {
        Client c = new Client();
        c.connectar();
        c.rebreFitxers();
        c.tancarConnexio();
        System.out.println("Connexio tancada.");
    }
}
