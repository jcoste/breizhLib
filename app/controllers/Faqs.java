package controllers;

import controllers.security.Secure;
import models.Faq;
import models.Widget;
import models.socialoauth.Role;
import play.data.validation.Required;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With({Secure.class,Widgets.class})
public class Faqs  extends Controller {

    @Role("public")
    @Get("/faq")
    public static void index() {
        List<Faq> faqs = Faq.findAll();
        render(faqs);
    }

    @Role("admin")
    @Get("/faq/add")
    public static void add() {
        render();
    }
    @Role("admin")
       public static void up(Long id){
          Faq faq = Faq.findById(id);
          if(faq != null && faq.order > 0){
             int order = faq.order;
             List<Faq> faqs = Faq.findAll();
              for(Faq pFaq : faqs){
                  if(pFaq.order == order-1 ){
                      pFaq.order = pFaq.order +1;
                      pFaq.save();
                  }
              }
              faq.order = order -1;
              faq.save();
          }

          index();
       }

       @Role("admin")
       public static void down(Long id){
          Faq faq = Faq.findById(id);
          List<Faq> faqs = Faq.findAll();
           if(faq != null && faq.order != faqs.size()-1  ){
                     int order = faq.order;

                      for(Faq pFaq : faqs){
                          if(pFaq.order == order+1 ){
                              pFaq.order = pFaq.order -1;
                              pFaq.save();
                          }
                      }
                      faq.order = order +1;
                      faq.save();
                  }

          index();
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
        faq.order =  Faq.findAll().size()-1;
        faq.save();

        index();
    }
}
