package models.tag;


import models.Livre;
import siena.*;

import java.util.ArrayList;
import java.util.List;

@siena.Table("LivreTag")
public class LivreTag extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @NotNull
    @Column("livre_id")
    @Index("livre_index")
    public Livre livre;

    @NotNull
    @Column("tag_id")
    @Index("tag_index")
    public Tag tag;

    public LivreTag(Livre livre, Tag tag) {
        super();
        this.livre = livre;
        this.tag = tag;
    }

    public static Query<LivreTag> all() {
        return Model.all(LivreTag.class);
    }

    public static List<Tag> findByLivre(Livre livre) {
        List<LivreTag> livreTags = all().filter("livre", livre).fetch();
        List<Tag> tags = new ArrayList<Tag>();
        for (LivreTag livreTag : livreTags) {
            tags.add(Tag.findById(livreTag.tag.id));
        }

        return tags;
    }

    public static List<Livre> findByTag(Tag tag) {
        List<LivreTag> livreTags = all().filter("tag", tag).fetch();
        List<Livre> links = new ArrayList<Livre>();
        for (LivreTag livreTag : livreTags) {
            links.add(Livre.findById(livreTag.livre.id));
        }

        return links;
    }

    public static LivreTag findByTagAndLivre(Tag tag, Livre livre) {
        return all().filter("tag", tag).filter("livre", livre).get();
    }
}
