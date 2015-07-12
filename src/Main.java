import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    private static HashMap<String, MyUrl> frontierContents;
    private static PriorityQueue<MyUrl> frontier;
    private static HashMap<String, MyUrl> visitedPages;
    private static final int MAX_PAGES = 3000;
    private static long startTime;
    private static Index index;
    private static PageFilter pageFilter;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();

        index = new Index();
        pageFilter = new PageFilter();
        frontierContents = new HashMap<String, MyUrl>();
        visitedPages = new HashMap<String, MyUrl>();
        frontier = new PriorityQueue<MyUrl>(new Comparator<MyUrl>() {
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

        // add seed urls

        // MyUrl seed1 = new MyUrl(
        // canonicalize("http://www.cellarer.com/best-cooking-blogs"),
        // System.currentTimeMillis(), true);
        // MyUrl seed2 = new MyUrl(
        // canonicalize("http://www.pbs.org/food/features/best-of-2013-review-food-blogs"),
        // System.currentTimeMillis(), true);
        // MyUrl seed3 = new MyUrl(
        // canonicalize("http://www.bhg.com/blogs/better-homes-and-gardens-blogger-awards-/top-everyday-eats-blogs/"),
        // System.currentTimeMillis(), true);
        // MyUrl seed4 = new MyUrl(
        // canonicalize("http://www.huffingtonpost.com/news/best-food-blogs"),
        // System.currentTimeMillis(), true);
        MyUrl seed5 = new MyUrl(
                canonicalize("http://best.king5.com/best/food-blog/online-and-social-media/western-washington"),
                System.currentTimeMillis(), true);

        // frontier.add(seed1);
        // frontierContents.put(seed1.getUrl(), seed1);
        // frontier.add(seed2);
        // frontierContents.put(seed2.getUrl(), seed2);
        // frontier.add(seed3);
        // frontierContents.put(seed3.getUrl(), seed3);
        // frontier.add(seed4);
        // frontierContents.put(seed4.getUrl(), seed4);
        frontier.add(seed5);
        frontierContents.put(seed5.getUrl(), seed5);

        // crawl
        int counter = 1;
        long timeOfLastCrawl = 0;
        while (!frontier.isEmpty() && (visitedPages.size() < MAX_PAGES)) {
            // get first element in frontier
            MyUrl nextElement = frontier.poll();
            String nextUrl = nextElement.getUrl();

            if (visitedPages.containsKey(nextUrl)) {
                continue;
            }

            if (CrawlerCommons.isCrawlable(nextUrl, "crawler_steve")
                    && pageFilter.isCrawlable(nextUrl)) {
                System.out.println("Crawling page " + counter + ": " + nextUrl);
                try {
                    long delay = CrawlerCommons.getCrawlDelay(nextUrl,
                            "crawler_steve");
                    if (delay >= 1) {
                        Thread.sleep(delay * 1000);
                    } else {
                        long timeSinceLastCrawl = System.currentTimeMillis()
                                - timeOfLastCrawl;
                        if (timeSinceLastCrawl < 1000) {
                            Thread.sleep(1000 - timeSinceLastCrawl);
                        }
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                try {
                    // Get the raw html, text, and outlinks
                    System.out.println("\tConnecting to page...");
                    Document doc = Jsoup.connect(nextUrl).get();
                    timeOfLastCrawl = System.currentTimeMillis();

                    Element body = doc.body();
                    String html = body.outerHtml();
                    String cleanText = body.text();
                    ArrayList<String> outlinks = cleanUrls(doc
                            .select("a[href]"));
                    WebPage webpage = new WebPage(nextUrl, html, cleanText,
                            outlinks);

                    // process outlinks
                    System.out.println("\tProcessing outlinks...");
                    for (String url : webpage.getOutlinks()) {
                        processOutlink(url, nextUrl);
                    }

                    // index the webpage
                    System.out.println("\tIndexing webpage...");
                    index.indexPage(webpage);

                } catch (HttpStatusException hse) {
                    visitedPages.put(nextUrl, nextElement);
                    frontierContents.remove(nextUrl);
                    continue;
                } catch (SocketTimeoutException ste) {
                    visitedPages.put(nextUrl, nextElement);
                    frontierContents.remove(nextUrl);
                    continue;
                } catch (UnsupportedMimeTypeException u) {
                    continue;
                } catch (NullPointerException n) {
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            visitedPages.put(nextUrl, nextElement);
            frontierContents.remove(nextUrl);
            counter++;
        }

        addInLinks();
        index.close();
    }

    private static void addInLinks() {
        for (String url : visitedPages.keySet()) {
            index.addInLinks(visitedPages.get(url));
        }
    }

    private static void testResult() {
        System.out.println();
        for (String url : visitedPages.keySet()) {
            System.out.print(visitedPages.get(url).prettyPrint());
        }
        System.out.println("Number of visitedPages: " + visitedPages.size());
        System.out.println();
        System.out.println("Number of pages still in frontier: "
                + frontier.size());
        System.out.println();
        System.out.println("Runtime: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void processOutlink(String url, String sourceOfLink) {
        if (visitedPages.containsKey(url)) {
            visitedPages.get(url).addInLink(sourceOfLink);
        } else if (frontierContents.containsKey(url)) {
            MyUrl page = frontierContents.get(url);
            frontier.remove(page);
            frontierContents.remove(url);
            page.addInLink(sourceOfLink);
            frontier.add(page);
            frontierContents.put(url, page);
        } else {
            MyUrl page = new MyUrl(url, System.currentTimeMillis(), false);
            page.addInLink(sourceOfLink);
            frontier.add(page);
            frontierContents.put(url, page);
        }
    }

    private static ArrayList<String> cleanUrls(Elements outlinks) {
        ArrayList<String> cleanOutlinks = new ArrayList<String>();

        for (Element link : outlinks) {
            String url = link.attr("abs:href");
            if (url.equals("")) {
                continue;
            }
            url = canonicalize(url);
            cleanOutlinks.add(url);
        }

        return cleanOutlinks;
    }

    public static String canonicalize(String urlString) {
        String result = null;
        try {
            URL url = new URL(urlString.toLowerCase());
            result = url.getProtocol() + "://" + url.getHost() + url.getPath();
            result.replaceAll("/+", "/").replaceFirst("/", "//");
        } catch (MalformedURLException e) {
            System.out.println("Malformed url: " + urlString);
        }
        return result;
    }
}
