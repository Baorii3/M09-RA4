import java.io.*;
import java.net.*;

public class Servidor {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        Socket s = serverSocket.accept();
        System.out.println("Connexio acceptada: " + s.getRemoteSocketAddress());
        return s;
    }

    public void enviarFitxers(Socket s) {
        try (ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
            oos.flush();

            System.out.println("Esperant el nom del fitxer del client...");
            Object obj = ois.readObject();
            if (!(obj instanceof String) || obj == null || ((String) obj).isBlank()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            String rutaFitxer = (String) obj;
            System.out.println("Nomfitxer rebut: " + rutaFitxer);

            File fitxer = new File(rutaFitxer);
            if (!fitxer.exists() || !fitxer.isFile()) {
                System.out.println("Fitxer no trobat: " + rutaFitxer);
                oos.writeObject(null);
                oos.flush();
                return;
            }

            Fitxer f = new Fitxer(rutaFitxer);
            System.out.println("Contingut del fitxer a enviar: " + f.getContingut().length + " bytes");
            oos.writeObject(f.getContingut());
            oos.flush();
            System.out.println("Fitxer enviat al client: " + rutaFitxer);
        } catch (Exception e) {
            System.out.println("Error llegint el fitxer del client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void tancarConnexio(Socket s) {
        try {
            if (s != null) s.close();
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws Exception {
        Servidor srv = new Servidor();
        Socket s = srv.connectar();
        try {
            srv.enviarFitxers(s);
        } finally {
            srv.tancarConnexio(s);
            System.out.println("Tancant connexio amb el client: " + (s.getRemoteSocketAddress()));
            if (srv.serverSocket != null) srv.serverSocket.close();
        }
    }
}
