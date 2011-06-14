package controllers.tag;

import controllers.Livres;
import controllers.security.Role;
import controllers.security.Secure;
import models.Livre;
import models.tag.LivreTag;
import models.tag.Tag;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Tags extends Controller {

    @Role("admin")
    public static void addForBook(String bookId,String tag) {

       Livre livre = Livre.findByISBN(bookId);

       Tag newTag = Tag.findOrCreateByName(tag);

       LivreTag livreTag = new LivreTag(livre, newTag);
       livreTag.insert();
       Livres.show(bookId);
    }

    @Role("public")
    public static void index() {
       List<LivreTag> tags = LivreTag.all().fetch();
       for(LivreTag livreTag : tags) {
           livreTag.tag.get();
       }
       render(tags);
    }
}
