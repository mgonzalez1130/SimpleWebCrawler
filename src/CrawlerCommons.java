import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;
import crawlercommons.robots.SimpleRobotRulesParser;

public class CrawlerCommons {

    public static long getCrawlDelay(String page_url, String user_agent) {
        try {
            URL urlObj = new URL(page_url);
            String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
                    + (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
            // System.out.println(hostId);
            Map<String, BaseRobotRules> robotsTxtRules = new HashMap<String, BaseRobotRules>();
            BaseRobotRules rules = robotsTxtRules.get(hostId);
            if (rules == null) {
                String robotsContent = getContents(hostId + "/robots.txt");
                if (robotsContent == null) {
                    rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
                } else {
                    SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
                    rules = robotParser.parseContent(hostId,
                            robotsContent.getBytes(), "text/plain", user_agent);
                }
            }
            return rules.getCrawlDelay();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean isCrawlable(String page_url, String user_agent) {
        try {
            URL urlObj = new URL(page_url);
            String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
                    + (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
            // System.out.println(hostId);
            Map<String, BaseRobotRules> robotsTxtRules = new HashMap<String, BaseRobotRules>();
            BaseRobotRules rules = robotsTxtRules.get(hostId);
            if (rules == null) {
                String robotsContent = getContents(hostId + "/robots.txt");
                if (robotsContent == null) {
                    rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
                } else {
                    SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
                    rules = robotParser.parseContent(hostId,
                            robotsContent.getBytes(), "text/plain", user_agent);
                }
            }
            return rules.isAllowed(page_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getContents(String page_url) {
        InputStream is = null;
        try {
            URLConnection openConnection = new URL(page_url).openConnection();
            openConnection
                    .addRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            openConnection.setConnectTimeout(3000);
            openConnection.setReadTimeout(3000);
            is = openConnection.getInputStream();
            String theString = IOUtils.toString(is);
            return theString;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
