package models.tag;

import siena.Generator;
import siena.Id;
import siena.Model;

@siena.Table("Tag")
public class Tag extends Model implements Comparable<Tag> {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public String name;

    public transient int nb;

    private Tag(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public int compareTo(Tag otherTag) {
        return name.compareTo(otherTag.name);
    }

    public static Tag findOrCreateByName(String name) {
        Tag tag = Tag.all(Tag.class).filter("name", name).get();
        if (tag == null) {
            tag = new Tag(name);
            tag.insert();
        }
        return tag;
    }

    public static Tag findById(Long tagId) {
        return Tag.all(Tag.class).filter("id", tagId).get();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (((Tag) that).name == null) {
            return false;
        }

        if (((Tag) that).name.equals(this.name)) {
            return true;
        }

        return false;

    }
}
