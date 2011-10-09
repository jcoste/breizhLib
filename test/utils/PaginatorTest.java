package utils;

import org.junit.Test;
import play.test.FunctionalTest;
import siena.*;
import siena.core.async.QueryAsync;
import siena.core.options.QueryOption;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PaginatorTest extends FunctionalTest {

    @Test
    public void getElements() {
        Query query = new QueryStub();
        Paginator<TestModel> paginator = new Paginator<TestModel>(2, 0, "test", query);

        List<TestModel> result = paginator.getElements();

        assertNotNull(result);

    }

    @Test
    public void nextLabel(){
        Query query = new QueryStub();
        Paginator<TestModel> paginator = new Paginator<TestModel>(2, 0, "test", query);

        assertEquals("3 - 4",paginator.nextLabel());
    }

     @Test
    public void hasPreviousPage(){
        Query query = new QueryStub(10);
        Paginator<TestModel> paginator = new Paginator<TestModel>(2, 1, "test", query);

        assertTrue(paginator.hasPreviousPage());
    }

     @Test
    public void hasNextPage(){
        Query query = new QueryStub(10);
        Paginator<TestModel> paginator = new Paginator<TestModel>(3, 0, "test", query);

        assertTrue(paginator.hasNextPage());
    }

     @Test
    public void pagination(){

         assertEquals(5,Paginator.pagination(1,10,5));

    }




    class TestModel extends Model {

    }

    class QueryStub<T> implements Query<T>  {

        int count = 0;
        public QueryStub(int count) {
            this.count = count;
        }

        public QueryStub() {

        }

        public Query filter(String s, Object o) {
            return null;
        }

        public Query order(String s) {
            return null;
        }

        public Query join(String s, String... strings) {
            return null;
        }

        public Query search(String s, String... strings) {
            return null;
        }

        public Query search(String s, QueryOption queryOption, String... strings) {
            return null;
        }

        public T get() {
            return null;
        }

        public int delete() {
            return 0;
        }

        public int update(Map<String, ?> stringMap) {
            return 0;
        }

        public int count() {
            return count;
        }

        public T getByKey(Object o) {
            return null;
        }

        public List fetch() {
            return null;
        }

        public List fetch(int i) {
            return null;
        }

        public List fetch(int i, Object o) {
            return new ArrayList();
        }

        public List fetchKeys() {
            return null;
        }

        public List fetchKeys(int i) {
            return null;
        }

        public List fetchKeys(int i, Object o) {
            return null;
        }

        public Iterable iter() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Iterable iter(int i) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Iterable iter(int i, Object o) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Iterable iterPerPage(int i) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query clone() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query limit(int i) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query offset(Object o) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query paginate(int i) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query nextPage() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query previousPage() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query customize(QueryOption... queryOptions) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query stateful() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query stateless() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query release() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query resetData() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String dump(QueryOption... queryOptions) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dump(OutputStream outputStream, QueryOption... queryOptions) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query restore(String s, QueryOption... queryOptions) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query restore(InputStream inputStream, QueryOption... queryOptions) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public QueryAsync async() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public PersistenceManager getPersistenceManager() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int count(int i) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int count(int i, Object o) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object nextOffset() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void setNextOffset(Object o) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Query search(String s, boolean b, String s1) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public QueryOption option(int i) {
            return null;
        }

        public Map<Integer, QueryOption> options() {
            return null;
        }

        public List<QueryFilter> getFilters() {
            return null;
        }

        public List<QueryOrder> getOrders() {
            return null;
        }

        public List<QueryFilterSearch> getSearches() {
            return null;
        }

        public List<QueryJoin> getJoins() {
            return null;
        }

        public Class getQueriedClass() {
            return null;
        }
    }
}
