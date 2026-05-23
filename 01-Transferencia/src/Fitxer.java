import java.io.*;

public class Fitxer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private byte[] contingut;

    public Fitxer(String path) throws IOException {
        File f = new File(path);
        this.nom = f.getName();
        try (FileInputStream in = new FileInputStream(f);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int r;
            while ((r = in.read(buf)) != -1) baos.write(buf, 0, r);
            this.contingut = baos.toByteArray();
        }
    }

    public String getNom() {
        return nom;
    }

    public byte[] getContingut() {
        return contingut;
    }
}
