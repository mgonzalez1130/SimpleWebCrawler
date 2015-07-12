import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class testJsoup {

    public static void main(String args[]) {

        try {
            URL url = new URL("http://www.foreignaffairs.com/");
            System.out.println(url.getHost());
            System.out.println(url.getAuthority());
            System.out.println(url.getPath());
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Document doc = null;
        try {
            doc = Jsoup.connect(
                    "http://advertising.aol.com/brands/huffington-post").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            System.out.println("\nlink : " + link.attr("abs:href"));
            System.out.println("text : " + link.text());
        }

    }

}
