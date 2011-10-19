package models;


import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.util.List;

@siena.Table("Faq")
public class Faq extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String question;

    public String reponse;

     public int order;


    public Faq(String question, String reponse) {
        this.question = question;
        this.reponse = reponse;
    }


     public static List<Faq> findAll() {
        return Faq.all(Faq.class).order("order").fetch();
    }

    public static Faq findById(Long id) {
        return Faq.all(Faq.class).filter("id", id).get();
    }
}
