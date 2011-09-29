package remote;

import models.Livre;
import org.junit.Test;


public class IsbnExtractorTest {

    @Test
    public void testFind(){
        Livre livre = IsbnNicebooksExtractor.getLivre("978-2-7440-2119-0");

        System.out.println(livre.iSBN);
        System.out.println(livre.titre);
        System.out.println(livre.editeur);
        System.out.println(livre.image);
    }

    @Test
    public void testisbn(){
         String isbn = "9782212123630";
         String result =  isbn.substring(0,3)+"-"+isbn.substring(3,4)+"-"+isbn.substring(4,8)+"-"+isbn.substring(8,12)+"-"+isbn.substring(12,13);
         System.out.println(result);
    }
}


