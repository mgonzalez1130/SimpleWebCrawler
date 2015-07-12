import java.util.ArrayList;

public class MyUrl {

    String url;
    Long timeStamp;
    boolean seed;
    ArrayList<String> inLinks;

    public MyUrl(String url, Long timeStamp, boolean seed) {
        this.url = url;
        this.timeStamp = timeStamp;
        this.seed = seed;
        this.inLinks = new ArrayList<String>();
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public boolean isSeed() {
        return seed;
    }

    public String getUrl() {
        return url;
    }

    public Integer getNumInLinks() {
        return inLinks.size();
    }

    public ArrayList<String> getInLinks() {
        return inLinks;
    }

    public void addInLink(String url) {
        if (!inLinks.contains(url)) {
            inLinks.add(url);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (url == null ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyUrl other = (MyUrl) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    public String prettyPrint() {
        return "Url: " + url + "\n" + "Time crawled: " + timeStamp + "\n"
                + "Seed? " + seed + "\n" + "Number of inLinks: "
                + inLinks.size() + "\n" + printInLinks() + "\n";
    }

    private String printInLinks() {
        String result = "";

        int counter = 1;
        for (String url : inLinks) {
            result += "InLinks " + counter + ": " + url + "\n";
            counter++;
        }

        return result;
    }

}
