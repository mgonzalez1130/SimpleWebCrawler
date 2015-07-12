import java.util.ArrayList;

public class WebPage {

    private String url;
    private String rawHtml;
    private String cleanText;
    private ArrayList<String> outlinks;

    public WebPage(String url, String rawHtml, String cleanText,
            ArrayList<String> outlinks) {
        this.url = url;
        this.rawHtml = rawHtml;
        this.cleanText = cleanText;
        this.outlinks = outlinks;
    }

    public String getUrl() {
        return url;
    }

    public String getRawHtml() {
        return rawHtml;
    }

    public String getCleanText() {
        return cleanText;
    }

    public ArrayList<String> getOutlinks() {
        return outlinks;
    }

}
