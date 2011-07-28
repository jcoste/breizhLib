package remote;

import models.Livre;
import org.junit.Test;


public class IsbnExtractorTest {

    @Test
    public void testFind(){
        Livre livre = Isbn13Extractor.getLivre("9782212123630");

        System.out.println(livre.iSBN);
        System.out.println(livre.titre);
        System.out.println(livre.editeur);
        System.out.println(livre.image);
    }
}
