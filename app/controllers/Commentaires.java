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
import java.util.Collections;
import java.util.Comparator;
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
    @Get("/commentaires/editeur/{editeur}/{tri}-{page}")
    public static void editeur(String editeur, int page,String tri) {
        String triSearch = tri;
        if(triSearch == null || tri.equals("date")){
            tri = "date";
            triSearch = "-dateAjout";
        } else {
            triSearch = "-dateAjout";
        }

        int max = 0;
        for (Commentaire commentaire : Commentaire.findAll()) {
            commentaire.livre.get();
            if (commentaire.livre.editeur.equals(editeur)) {
                max++;
            }
        }

        int dept = NB_PAR_PAGE;
        int debut = Paginator.pagination(page, max, NB_PAR_PAGE);

        List<Commentaire> commentairesAllByPage = Commentaire.all(Commentaire.class).order(triSearch).fetch(dept, debut);

        final String triCmp = tri;
       Collections.sort(commentairesAllByPage,new Comparator<Commentaire>() {
           @Override
           public int compare(Commentaire commentaire, Commentaire commentaire1) {
               commentaire.livre.get();
               commentaire1.livre.get();
               if(triCmp.equals("titre")){
                    return commentaire.livre.titre.compareTo(commentaire1.livre.titre);
               }else if(triCmp.equals("popularite")){
                    return commentaire.livre.popularite.compareTo(commentaire1.livre.popularite);
               }
               return 0;
           }
       });

        List<Commentaire> commentaires = new ArrayList<Commentaire>();
        for (Commentaire commentaire : commentairesAllByPage) {
            commentaire.livre.get();
            if (commentaire.livre.editeur.equals(editeur)) {
                commentaires.add(commentaire);
            }
        }

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(page, dept, max, editeur,commentaires,tri);
    }

    @Get("/commentaires/{tri}-{page}")
    public static void index(int page,String tri) {
        String triSearch = tri;
        if(triSearch == null || tri.equals("date")){
            tri = "date";
            triSearch = "-dateAjout";
        } else {
            triSearch = "-dateAjout";
        }


        Query<Commentaire> query = Commentaire.all(Commentaire.class).order(triSearch);
        Paginator<Commentaire> paginator = new Paginator<Commentaire>(NB_PAR_PAGE, page, "Commentaires.index", query);

        final String triCmp = tri;
        Collections.sort(paginator.getElements(),new Comparator<Commentaire>() {
           @Override
           public int compare(Commentaire commentaire, Commentaire commentaire1) {
               commentaire.livre.get();
               commentaire1.livre.get();
               if(triCmp.equals("titre")){
                    return commentaire.livre.titre.compareTo(commentaire1.livre.titre);
               }else if(triCmp.equals("popularite")){
                    return commentaire.livre.popularite.compareTo(commentaire1.livre.popularite);
               }
               return 0;
           }
       });

        for (Commentaire commentaire : paginator.getElements()) {
            commentaire.livre.get();
        }

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(paginator,tri);
    }

    @Role("public")
    @Get(value = "/commentaires.xml", format = "xml")
    public static void all() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch();

        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires);
    }
}
