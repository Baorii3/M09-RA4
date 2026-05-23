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
        try (ObjectOutputStream sortida = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(s.getInputStream())) {
            sortida.flush();

            System.out.println("Esperant el nom del fitxer del client...");
            while (true) {
                Object missatge = entrada.readObject();
                if (missatge == null) {
                    System.out.println("Client ha tancat la connexio.");
                    break;
                }

                if (!(missatge instanceof String)) {
                    System.out.println("Rebut objecte no vàlid. Enviant null.");
                    sortida.writeObject(null);
                    sortida.flush();
                    continue;
                }

                String rutaFitxer = (String) missatge;
                if (rutaFitxer.isBlank()) {
                    System.out.println("Nom del fitxer buit. Ignorant...");
                    sortida.writeObject(null);
                    sortida.flush();
                    continue;
                }

                if (rutaFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Client ha demanat sortir. Tancant connexio...");
                    break;
                }

                System.out.println("Nomfitxer rebut: " + rutaFitxer);

                File fitxer = new File(rutaFitxer);
                if (!fitxer.exists() || !fitxer.isFile()) {
                    System.out.println("Fitxer no trobat: " + rutaFitxer);
                    sortida.writeObject(null);
                    sortida.flush();
                    continue;
                }

                Fitxer fitxerAEnviar = new Fitxer(rutaFitxer);
                System.out.println("Contingut del fitxer a enviar: " + fitxerAEnviar.getContingut().length + " bytes");
                sortida.writeObject(fitxerAEnviar.getContingut());
                sortida.flush();
                System.out.println("Fitxer enviat al client: " + rutaFitxer);
            }
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
        srv.serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexions...");
        while (true) {
            Socket s = srv.serverSocket.accept();
            System.out.println("Connexio acceptada: " + s.getRemoteSocketAddress());
            try {
                srv.enviarFitxers(s);
            } finally {
                srv.tancarConnexio(s);
                System.out.println("Tancant connexio amb el client: " + (s.getRemoteSocketAddress()));
            }
        }
    }
}
