package controllers;


import controllers.security.Secure;
import models.Commentaire;
import models.Widget;
import models.WidgetData;
import models.socialoauth.Role;
import models.tag.LivreTag;
import models.tag.Tag;
import play.data.validation.Required;
import play.modules.router.Post;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.ArrayList;
import java.util.List;

@With(Secure.class)
public class Widgets extends Controller {

    @Before
    public static void init() {

        WidgetData data = new WidgetData();

        List<LivreTag> tagLivres = LivreTag.all().fetch();
        List<Tag> tags = new ArrayList<Tag>();
        for (LivreTag livreTag : tagLivres) {
            livreTag.tag.get();
            if (!tags.contains(livreTag.tag)) {
                livreTag.tag.nb = 1;
                tags.add(livreTag.tag);
            } else {
                int i = tags.indexOf(livreTag.tag);
                tags.get(i).nb++;
            }
        }
        data.put("tags", tags);
        data.put("action",request.action );

        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch(Commentaires.NB_NEWS_PAR_PAGE);
        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
            commentaire.user.get();
        }
        data.put("lcommentaires", commentaires);

        data.widgets = Widget.findAll();


        renderArgs.put("widget", data);
    }

    @Role("admin")
    public static void admin(){
        List<String> roles = new ArrayList<String>();
        roles.add("public");
        roles.add("member");
        roles.add("admin");

        List<Widget> widgets =Widget.findAll();
        render(roles,widgets);
    }

     @Role("admin")
    public static void delete(Long id){
       Widget widget = Widget.findById(id);
       if(widget != null){
           widget.delete();
       }
       admin();
    }

    @Post("/Widgets/add")
    public static void save(@Required String titre,@Required String template, @Required String role){
         if (validation.hasErrors()) {
            render("Widgets/add.html");
        }

        Widget widget = Widget.findByTitre(titre);

        if(widget == null){
            widget = new Widget(titre,template,role);
            widget.insert();
        }
        admin();
    }
}


