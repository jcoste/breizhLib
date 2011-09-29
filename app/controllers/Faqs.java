package controllers;

import models.Faq;
import play.data.validation.Required;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;

import java.util.List;


public class Faqs  extends Controller {

    @Get("/faq")
    public static void index() {
        List<Faq> faqs = Faq.all(Faq.class).fetch();
        render(faqs);
    }

    @Get("/faq/add")
    public static void add() {
        render();
    }

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
