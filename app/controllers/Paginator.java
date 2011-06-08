package controllers;


import play.mvc.Router;
import play.mvc.Scope;
import siena.Model;
import siena.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Paginator<E extends Model> {

    public int nbParPage = 4;
    public int max = 0;
    public int page = 0;
    public String action;
    public Query<E> query;
    public List<E> elements;

    private final Map<String, Object> viewParams;

   public Paginator(int nbParPage,int page,String action,Query<E> query){
        this.query = query;
        this.max = query.count();
        this.nbParPage = nbParPage;
        this.page = page;
        this.action = action;
        this.viewParams = new HashMap<String, Object>();
        Scope.Params params = Scope.Params.current();
        if (params != null) {
            this.viewParams.putAll(params.allSimple());
        }
        this.viewParams.remove("body");
   }

    public List<E> getElements() {
       if(elements == null){
            elements = query.fetch(nbParPage, pagination(page, max, nbParPage));
        }
        return elements;
    }

    public String previous(){
        viewParams.put("page", String.valueOf(page-1));
        return Router.reverse(action, viewParams).url;
    }

    public boolean hasPreviousPage(){
        return page > 0;
    }

    public String previousLabel(){
        return (((page*nbParPage)-nbParPage)+1)+" - "+(page*nbParPage);
    }

    public String next(){
        viewParams.put("page", String.valueOf(page+1));
        return Router.reverse(action, viewParams).url;
    }

    public boolean hasNextPage() {
        return max > ((page+1)*nbParPage);
    }

    public String nextLabel(){
        return ((page+1)*nbParPage)+1+" - "+(((page+1)*nbParPage)+nbParPage);
    }



    public static int pagination(int page, int max, int nbParPage) {
        if (page < 0) {
            page = 0;
        }

        int dept = nbParPage;
        int debut = (page * dept);
        if (debut >= max) {
            debut = max - (max - dept) / dept;
            page = debut / dept;
        }
        return debut;
    }

}
