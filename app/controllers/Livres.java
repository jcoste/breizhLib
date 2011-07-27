package controllers;


import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.data.validation.Required;
import play.i18n.Messages;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;
import remote.IsbnExtractor;
import siena.Query;
import utils.Paginator;

import java.util.List;

@With(Secure.class)
public class Livres extends Controller {

    private static int NB_PAR_PAGE = 10;

    private static int NB_NEWS_PAR_PAGE = 5;


    @Role("public")
    @Get("/books/{tri}/{page}")
    public static void index(int page, String tri) {
        String triSearch = tri;
        if (triSearch == null || tri.equals("date")) {
            triSearch = "-dateAjout";
        } else if (tri.equals("popularite")) {
            triSearch = "-popularite";
        }
        Query<Livre> query = Livre.all(Livre.class).order(triSearch);
        Paginator<Livre> paginator = new Paginator<Livre>(NB_PAR_PAGE, page, "Livres.index", query);

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(paginator, tri);
    }

    @Role("public")
    @Get(value = "/books.xml", format = "xml")
    public static void all() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        for (Livre livre : livres) {
            livre.popularite = livre.getCommentaires().size();
            livre.update();
        }

        render(livres);
    }

    @Role("public")
    @Get(value = "/ouvrages/", format = "xml")
    public static void allXml() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        render(livres);
    }

    @Role("public")
    @Get(value = "/ouvrages.json", format = "json")
    public static void allJson() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        render(livres);
    }


    @Role("public")
    @Get("/books/editeur/{editeur}/{tri}/{page}")
    public static void editeur(String editeur, int page, String tri) {
        String triSearch = tri;
        if (triSearch == null || tri.equals("date")) {
            triSearch = "-dateAjout";
        } else if (tri.equals("popularite")) {
            triSearch = "-popularite";
        }

        Query<Livre> livres = Livre.all(Livre.class).filter("editeur", editeur).order(triSearch);
        Paginator<Livre> paginator = new Paginator<Livre>(NB_PAR_PAGE, page, "Livres.editeur", livres);

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(editeur, paginator, tri);
    }


    @Role("public")
    @Get("/news")
    public static void last() {
        List<Livre> livres = Livre.all(Livre.class).order("-dateAjout").fetch(NB_NEWS_PAR_PAGE);
        render(livres);
    }

    @Role("public")
    @Get("/book/{id}")
    public static void show(String id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findByISBN(id);
        User user = (User) Secure.getUser();
        render(livre, user);
    }


    @Role("admin")
    @Get("/book/{id}/edit")
    public static void edit(Long id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(livre);
    }

    @Role("public")
    @Get("/search")
    public static void search() {
        render();
    }

    @Role("admin")
    @Get("/add")
    public static void add() {
        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render();
    }

    @Role("admin")
    @Post("/add")
    public static void save(@Required String titre, @Required String editeur, byte[] imageFile, @Required String iSBN) throws Exception {
        if (validation.hasErrors()) {
            render("Livres/add.html");
        }

        if (Livre.all(Livre.class).filter("iSBN", iSBN).get() != null) {
            error(Messages.get("book_already_exist"));
        }

        //TODO contrôle de l'image, pour ne pas créer une image vide
        String image = null;
        if (imageFile != null) {
            Picture picture = Pictures.createImage(imageFile, "ouvrages/", iSBN, true);
            image = picture.getUrl();
            // Pictures.resizeImage(picture, 100, 133);
        }

        Livre livre = new Livre(titre, editeur, image, iSBN);
        livre.insert();
        show(livre.iSBN);
    }

    @Role("admin")
    @Post("/book/{iSBN}/edit")
    public static void update(@Required String titre, @Required String editeur, byte[] imageFile, @Required String iSBN) throws Exception {
        Livre livre = Livre.all(Livre.class).filter("iSBN", iSBN).get();
        if (validation.hasErrors()) {
            renderArgs.put("livre", livre);
            renderArgs.put("editeurs", Editeurs.initListEditeurs());
            render("Livres/edit.html");
        }

        if (livre == null) {
            error(Messages.get("book_not_exist"));
        }

        String image = null;
        if (imageFile != null) {
            Picture picture = Pictures.createImage(imageFile, "ouvrages/", iSBN, true);
            image = picture.getUrl();
            livre.image = image;
        }
        livre.editeur = editeur;
        livre.titre = titre;

        livre.update();
        show(livre.iSBN);
    }


    @Role("member")
    @Post("/book/{bookId}/comment")
    public static void postComment(String bookId, @Required String nom, @Required String content, @Required int note) {
        Livre livre = Livre.findByISBN(bookId);
        if (validation.hasErrors()) {
            render("Livres/show.html", livre);
        }


        User user = (User) Secure.getUser();
        Commentaire commentaire = new Commentaire(livre, user, nom, content, note);
        commentaire.insert();
        livre.popularite = livre.getCommentaires().size();
        livre.update();
        show(bookId);
    }

    @Role("member")
    @Post("/search")
    public static void postSearch(String recherche, String type) {
        if (recherche != null && recherche.length() > 0) {
            List<Livre> livres = Livre.findLikeTitre(recherche);

            List<Editeur> editeurs = Editeur.findLikeNom(recherche);

            List<Commentaire> commentaires = Commentaire.findLike(recherche);

            String message = null;
            if(livres.size() == 0 && editeurs.size() == 0 && commentaires.size() == 0){
              message = Messages.get("no_result");
            }

            render(livres, recherche, editeurs, commentaires, type,message);
        } else {
            String message = Messages.get("no_result");
            render(recherche, type,message);
        }
    }

    @Role("member")
    @Get("/book/{iSBN}/preview")
    public static void preview(String iSBN){
       String iSBN13 = iSBN.replaceAll("-","");
       render(iSBN13,iSBN);
    }


    @Role("public")
    @Post("/findisbn")
    public static void findisbn(String iSBN){
       String iSBN13 = iSBN.replaceAll("-","");

       List<Livre> livres =  Livre.findAll();
       for (Livre livre : livres) {
            if(livre.iSBN.replaceAll("-","").equals(iSBN13)){
                request.format = "json";
                render(livre);
            }
       }

       Livre livre = IsbnExtractor.getLivre(iSBN);
       livre.isNotPresent = true;
        request.format = "json";
        render(livre);
    }

    @Role("admin")
    @Post("/book/{iSBN}/validPreview")
    public static void validPreview(String iSBN,boolean valid){
        Livre livre = Livre.findByISBN(iSBN);
        if (livre == null) {
            render("Livres/preview.html", iSBN);
        }

        livre.preview = valid;
        livre.update();
         show(iSBN);
    }
}
