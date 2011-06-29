package controllers;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import play.modules.router.Any;
import play.mvc.Controller;

import java.io.StringWriter;

public class Textile extends Controller {

    // Controller classique Play! qui sera appelé par l'iframe Preview de MarkItUp.
    @Any("/textile/renderPreview")
    public static void renderPreview(String content) {
        StringWriter writer = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);

        // Ajout de mes 2 css.
        HtmlDocumentBuilder.Stylesheet css = new HtmlDocumentBuilder.Stylesheet("/public/stylesheets/main.css");
        builder.addCssStylesheet(css);
        css = new HtmlDocumentBuilder.Stylesheet("/public/stylesheets/wiki.css");
        builder.addCssStylesheet(css);

        // Création du parser Textile
        MarkupParser parser = new MarkupParser(new TextileLanguage());
        parser.setBuilder(builder);
        parser.parse(content);

        String htmlContent = writer.toString();
        renderText(htmlContent);
    }

    // Méthode qui sera appelée par les templates Groovy de Play!.
    public static String render(String wiki) {
        StringWriter writer = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
        // Empêche la génération des balises html et body.
        builder.setEmitAsDocument(false);

        // Création du parser Textile
        MarkupParser parser = new MarkupParser(new TextileLanguage());
        parser.setBuilder(builder);
        parser.parse(wiki);

        return writer.toString();
    }
}
