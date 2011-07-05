package controllers;

import controllers.security.Role;
import controllers.security.Secure;
import models.Commentaire;
import models.Livre;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;
import siena.Query;
import utils.Paginator;

import java.util.ArrayList;
import java.util.List;


@With(Secure.class)
public class Commentaires extends Controller {

    private static int NB_PAR_PAGE = 6;

    private static int NB_NEWS_PAR_PAGE = 4;

    @Get("/commentaires")
    public static void last() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch(NB_NEWS_PAR_PAGE);
        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires);
    }

    @Get("/book/{id}/commentaires")
    public static void commentaires(String id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findByISBN(id);
        List<Commentaire> commentaires = livre.getCommentaires();
        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires, livre);
    }


    @Role("public")
    @Get("/commentaires/editeur/{editeur}/{page}")
    public static void editeur(String editeur, int page) {
        int max = 0;
        for (Commentaire commentaire : Commentaire.findAll()) {
            commentaire.livre.get();
            if (commentaire.livre.editeur.equals(editeur)) {
                max++;
            }
        }

        int dept = NB_PAR_PAGE;
        int debut = Paginator.pagination(page, max, NB_PAR_PAGE);

        List<Commentaire> commentairesAllByPage = Commentaire.all(Commentaire.class).order("-dateAjout").fetch(dept, debut);

        List<Commentaire> commentaires = new ArrayList<Commentaire>();
        for (Commentaire commentaire : commentairesAllByPage) {
            commentaire.livre.get();
            if (commentaire.livre.editeur.equals(editeur)) {
                commentaires.add(commentaire);
            }
        }

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(page, dept, max, editeur);
    }

    @Get("/commentaires/{page}")
    public static void index(int page) {
        Query<Commentaire> query = Commentaire.all(Commentaire.class).order("-dateAjout");
        Paginator<Commentaire> paginator = new Paginator<Commentaire>(NB_PAR_PAGE, page, "Commentaires.index", query);

        for (Commentaire commentaire : paginator.getElements()) {
            commentaire.livre.get();
        }

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(paginator);
    }

    @Role("public")
    @Get(value = "/commentaires.xml",format = "xml")
    public static void all() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch();

        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires);
    }
}
