package remote;

import models.Livre;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class IsbnExtractor {

    private static final String AMAZON_URL = "http://www.amazon.fr/exec/obidos/ASIN/";
    private static final String IMAGE_PATTERN = "  registerImage\\(\"original_image\", \"([^\"]*)\".*";
    private static final String TITLE_PATTERN = "    <span id=\"btAsinTitle\">([^<]*) <.*";
    private static final String EDITOR_PATTERN = "<li><b>Editeur.:</b> ([^\\(]*) \\(.*";
    private static final String DESCRIPTION_BEFORE = "  <div class=\"productDescriptionWrapper\">";
    private static final String DESCRIPTION_AFTER = "  <div class=\"emptyClear\"> </div>";

    public static Livre getLivre(String isbn) {

        isbn = isbn.replaceAll("[- ]", "");

        try {
            Livre livre = new Livre();
            livre.iSBN = isbn;

            URL amazonUrl = new URL(AMAZON_URL + isbn);
            BufferedReader reader = new BufferedReader(new InputStreamReader(amazonUrl.openStream()));

            String line = reader.readLine();
            while (line != null) {
                if (line.matches(IMAGE_PATTERN)) {
                    livre.image = line.replaceFirst(IMAGE_PATTERN, "$1");
                }
                if (line.matches(TITLE_PATTERN)) {
                    livre.titre = line.replaceFirst(TITLE_PATTERN, "$1");
                }
                if (line.matches(EDITOR_PATTERN)) {
                    livre.editeur = line.replaceFirst(EDITOR_PATTERN, "$1");
                }
                //if (line.equals(DESCRIPTION_BEFORE) && livre.description == null) {
                //	StringBuffer buffer = new StringBuffer();
                //	line = reader.readLine();
                //	while (line != null && !line.equals(DESCRIPTION_AFTER)) {
                //		buffer.append(line);
                //		line = reader.readLine();
                //	}
                //	livre.description = buffer.toString();
                //}
                line = reader.readLine();
            }
            return livre;
        } catch (Exception ex) {
            // ISBN non trouvé
        }
        return null;
    }
}
