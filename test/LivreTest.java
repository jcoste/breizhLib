import models.Livre;
import org.junit.Test;
import play.test.UnitTest;
import utils.LoadDevData;

public class LivreTest extends UnitTest {

    @Test
    public void findAll() {
        LoadDevData.doJob();

        assertEquals(4, Livre.findAll().size());
    }

    public void findByIsnb(){
       LoadDevData.doJob();


       Livre livre = Livre.findByISBN("978-2-7440-2337-8");
       assertEquals("PEARSON",livre.editeur);
       assertEquals(1, livre.getTags().size());
    }

}
