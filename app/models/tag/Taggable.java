package models.tag;


import java.util.List;

public interface Taggable {

    List<Tag> getTags();

    void addTag(String tag);
}
