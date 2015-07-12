import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RobotsParser {

    public static void main(String args[]) {
        // "http://www.huffingtonpost.com/news/best-food-blogs")

        URL url = null;

        try {
            url = new URL("http://www.huffingtonpost.com/news/best-food-blogs");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String urlString = url.getProtocol() + "://" + url.getHost()
                + "/robots.txt";

        BaseRobotRules rules;
        byte[] content = null;

        try {
            InputStream in = url.openStream();
            content = Utils.getBytes(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if (Jsoup.connect(urlString).response().statusCode() == 404) {
        // rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
        // } else {
        SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
        rules = robotParser.parseContent(urlString, content, "text/plain",
                "teamClarke");
        // }

        System.out.println("crawl delay: " + rules.getCrawlDelay());
        for (String sitemap : rules.getSitemaps()) {
            System.out.println("Sitemap: " + sitemap);
        }

        System.out.println("/backstage/ : "
                + rules.isAllowed(url.getProtocol() + "://" + url.getHost()
                        + "/backstage/"));
        System.out
        .println("/social/ : "
                + rules.isAllowed(canonicalize("http://www.huffingtonpost.com/social/")));
        System.out
        .println("homepage : "
                + rules.isAllowed(canonicalize("http://www.huffingtonpost.com")));

        // HttpGet httpget = new HttpGet(hostId + "/robots.txt");
        // HttpContext context = new BasicHttpContext();
        // HttpResponse response = httpclient.execute(httpget, context);
        // if (response.getStatusLine() != null &&
        // response.getStatusLine().getStatusCode() == 404) {
        // rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
        // // consume entity to deallocate connection
        // EntityUtils.consumeQuietly(response.getEntity());
        // } else {
        // BufferedHttpEntity entity = new
        // BufferedHttpEntity(response.getEntity());
        // SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
        // rules = robotParser.parseContent(hostId,
        // IOUtils.toByteArray(entity.getContent()),
        // "text/plain", USER_AGENT);
        // }
    }

    public static String canonicalize(String urlString) {
        String result = null;
        try {
            URL url = new URL(urlString.toLowerCase());
            result = url.getProtocol() + "://" + url.getHost() + url.getPath();
        } catch (MalformedURLException e) {
            System.out.println("Malformed url: " + urlString);
        }
        return result;
    }

}
