package remote;


import models.Livre;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class IsbnNicebooksExtractor {

     private static final String URL = "http://fr.nicebooks.com/ISBN/";

    public static Livre getLivre(String isbn) {

        isbn =  isbn.replaceAll("-", "");

        try {
            Livre livre = new Livre();
            livre.iSBN = isbn;

            URL amazonUrl = new URL(URL + isbn);
            BufferedReader reader = new BufferedReader(new InputStreamReader(amazonUrl.openStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("/data/covers/medium/")) {
                    livre.image = line.substring(line.indexOf("<img src=") + 10   , line.indexOf("alt=")-2);
                }
                if (line.contains("<h1>")) {
                    livre.titre = line.substring(line.indexOf("<h1>") + "<h1>".length(), line.indexOf("</h1>"));
                }
                if (line.contains("<dt>Éditeur</dt>")) {
                    line = reader.readLine();
                    livre.editeur = line.substring(line.indexOf("\">") + 2, line.indexOf("</a></dd>"));
                }
            }
            return livre;
        } catch (Exception ex) {
            // ISBN non trouvé
        }
        return null;
    }
}
