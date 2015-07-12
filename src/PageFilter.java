import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PageFilter {

    private static ArrayList<String> pages = new ArrayList<String>();

    public PageFilter() {
        pages.add("advertising.aol.com");
        pages.add("twitter.com");
        pages.add("www.fark.com");
        pages.add("foreignaffairs.com");
        pages.add("mailto://");
    }

    public boolean isCrawlable(String url) {
        try {
            URL url2 = new URL(url);
            if (pages.contains(url2.getHost())) {
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
