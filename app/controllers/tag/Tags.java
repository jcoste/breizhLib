package controllers.tag;

import controllers.Livres;
import models.multioauth.Role;
import controllers.security.Secure;
import models.Livre;
import models.tag.LivreTag;
import models.tag.Tag;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Tags extends Controller {

    @Before
    public static void before(){

    }

    @Role("admin")
    @Post("/book/{bookId}/tag/{tag}")
    public static void addForBook(String bookId, String tag) {

        Livre livre = Livre.findByISBN(bookId);
        livre.addTag(tag.toUpperCase());

        Livres.show(bookId);
    }

    @Role("public")
    @Get("/tags")
    public static void index() {
        List<LivreTag> tags = LivreTag.all().fetch();
        for (LivreTag livreTag : tags) {
            livreTag.tag.get();
        }
        render(tags);
    }

    @Role("public")
    @Get("/tag/{tag}")
    public static void tag(String tag) {
        Tag tagBase = Tag.findOrCreateByName(tag);

        List<Livre> livres = LivreTag.findByTag(tagBase);
        render(tag, livres);
    }
}
