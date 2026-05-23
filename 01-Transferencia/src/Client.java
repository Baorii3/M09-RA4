import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 9999;

    private Socket socket;
    private ObjectOutputStream oos;

    public void connectar() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Connectant a -> " + HOST + ":" + PORT);
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    public void enviarFitxer(String path) {
        try {
            Fitxer f = new Fitxer(path);
            System.out.println("Enviant fitxer: " + path);
            oos.writeObject(f);
            oos.flush();
            System.out.println("Fitxer enviat.");
        } catch (Exception e) {
            System.out.println("Error enviant fitxer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void tancarConnexio() {
        try {
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws Exception {
        String path = null;
        if (args.length > 0) path = args[0];
        else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Nom del fitxer a enviar (ruta completa): ");
            path = sc.nextLine();
        }
        Client c = new Client();
        c.connectar();
        c.enviarFitxer(path);
        c.tancarConnexio();
    }
}
