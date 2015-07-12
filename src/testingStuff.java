import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class testingStuff {

    static HashMap<String, MyUrl> frontierContents;
    static PriorityQueue<MyUrl> testSet;

    public static void main(String[] args) {

        MyUrl url1 = new MyUrl("www.google.com", (long) 4, false);
        MyUrl url2 = new MyUrl("www.facebook.com", (long) 3, false);
        MyUrl url3 = new MyUrl("www.yahoo.com", (long) 2, false);
        MyUrl url4 = new MyUrl("www.gmail.com", (long) 1, false);

        // url1.addInLink("www.yahoo.com");
        System.out.print(url1.prettyPrint());

        frontierContents = new HashMap<String, MyUrl>();

        testSet = new PriorityQueue<MyUrl>(new Comparator<MyUrl>() {
            @Override
            public int compare(MyUrl url1, MyUrl url2) {
                if (url1.isSeed()) {
                    return -1;
                } else if (url2.isSeed()) {
                    return 1;
                }
                int res = url1.getNumInLinks().compareTo(url2.getNumInLinks())
                        * -1;

                if (res == 0) {
                    res = url1.getTimeStamp().compareTo(url2.getTimeStamp());
                }
                return res;
            }
        });

        addUrl(url1);
        addUrl(url2);
        addUrl(url3);
        addUrl(url4);

        printSet();

        testSet.poll();

        printSet();

        testSet.poll();
        printSet();

    }

    protected static void printSet() {
        System.out.println("Size: " + testSet.size());
        Iterator<MyUrl> it = testSet.iterator();
        while (it.hasNext()) {
            MyUrl next = it.next();
            System.out.println("Url: " + next.getUrl() + "  InLinks: "
                    + next.getNumInLinks());
        }
        System.out.println();
    }

    private static void addUrl(MyUrl url) {
        if (frontierContents.containsKey(url.getUrl())) {
            testSet.remove(frontierContents.get(url.getUrl()));
        }
        testSet.add(url);
        frontierContents.put(url.getUrl(), url);
    }
}
