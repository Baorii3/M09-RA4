import java.io.*;
import java.net.*;

public class Servidor {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public void connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
    }

    public Socket accepta() throws IOException {
        System.out.println("Esperant connexio...");
        Socket s = serverSocket.accept();
        System.out.println("Connexio acceptada: " + s.getRemoteSocketAddress());
        return s;
    }

    public void rebreFitxers(Socket s) {
        try (ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
            Object obj = ois.readObject();
            if (obj instanceof Fitxer) {
                Fitxer f = (Fitxer) obj;
                System.out.println("Nom del fitxer rebut: " + f.getNom());
                byte[] data = f.getContingut();
                File dir = new File("received");
                dir.mkdirs();
                File out = new File(dir, f.getNom());
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    fos.write(data);
                }
                System.out.println("Fitxer rebut i guardat com: " + out.getPath());
            } else {
                System.out.println("Objecte inesperat: " + obj);
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
        srv.connectar();
        try {
            Socket s = srv.accepta();
            srv.rebreFitxers(s);
            srv.tancarConnexio(s);
            System.out.println("Connexio tancada amb el client: " + (s.getRemoteSocketAddress()));
        } finally {
            if (srv.serverSocket != null) srv.serverSocket.close();
        }
    }
}
