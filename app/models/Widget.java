package models;

import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.util.List;

@siena.Table("Widget")
public class Widget extends Model {


    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String titre;

    public String role;

    public int order;

    public String template;

    public Widget(String titre, String template,String role) {
        super();
        this.titre = titre;
        this.template = template;
        this.role = role;
        this.order = 0;
    }


    public static Widget findByTitre(String titre) {
        return Widget.all(Widget.class).filter("titre", titre).get();
    }

    public static List<Widget> findAll() {
        return Widget.all(Widget.class).order("order").fetch();
    }

    public static Widget findById(Long id) {
        return Widget.all(Widget.class).filter("id", id).get();
    }


}
