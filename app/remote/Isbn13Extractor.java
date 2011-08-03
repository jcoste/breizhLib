package remote;

import models.Livre;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Isbn13Extractor {

    private static final String URL = "http://news.library.ryerson.ca/api/isbnsearch.php?isbn=";

    public static Livre getLivre(String isbn) {

        isbn = isbn.replaceAll("[- ]", "");

        try {
            Livre livre = new Livre();
            livre.iSBN = isbn;

            URL amazonUrl = new URL(URL + isbn);
            BufferedReader reader = new BufferedReader(new InputStreamReader(amazonUrl.openStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<thumbnail_url>")) {
                    //TODO sauvegarde de l'image partir de l'url
                    livre.image = line.substring(line.indexOf("<thumbnail_url>") + "<thumbnail_url>".length(), line.indexOf("</thumbnail_url>"));
                }
                if (line.contains("<title>")) {
                    livre.titre = line.substring(line.indexOf("<title>") + "<title>".length(), line.indexOf("</title>"));
                }
            }
            return livre;
        } catch (Exception ex) {
            // ISBN non trouvé
        }
        return null;
    }
}
