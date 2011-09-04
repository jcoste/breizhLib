package controllers;


import models.tag.LivreTag;
import models.tag.Tag;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class WidgetController extends Controller {

    @Before
    public static void init() {
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
        renderArgs.put("tags", tags);
    }
}
