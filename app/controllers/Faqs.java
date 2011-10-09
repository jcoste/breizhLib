package controllers;

import controllers.security.Secure;
import models.Faq;
import models.socialoauth.Role;
import play.data.validation.Required;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Faqs  extends Controller {

    @Role("public")
    @Get("/faq")
    public static void index() {
        List<Faq> faqs = Faq.all(Faq.class).fetch();
        render(faqs);
    }

    @Role("admin")
    @Get("/faq/add")
    public static void add() {
        render();
    }

    @Role("admin")
    @Post("/faq/post")
    public static void post(@Required String question,@Required String reponse) {

        if (validation.hasErrors()) {
            renderArgs.put("params.question", question);
            renderArgs.put("params.reponse", reponse);
            render("Faqs/add.html");
        }

        Faq faq = new Faq(question,reponse);
        faq.save();

        index();
    }
}
