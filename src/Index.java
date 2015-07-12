import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;

public class Index {

    private Client client;
    private ArrayList<String> emptyArrayList;

    @SuppressWarnings("resource")
    public Index() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "clarke").build();
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(
                        "10.0.0.12", 9300));
        emptyArrayList = new ArrayList<String>();
    }

    public void indexPage(WebPage page) {
        HashMap<String, Object> pageMap = new HashMap<String, Object>();

        pageMap.put("url", page.getUrl());
        pageMap.put("html", page.getRawHtml());
        pageMap.put("text", page.getCleanText());
        pageMap.put("in-links", emptyArrayList.toArray());
        pageMap.put("out-links", page.getOutlinks().toArray());

        client.prepareIndex("crawler_steve", "document", page.getUrl())
                .setSource(pageMap).execute().actionGet();
    }

    public void addInLinks(MyUrl url) {
        UpdateRequest update = new UpdateRequest();
        update.index("crawler_steve");
        update.type("document");
        update.id(url.getUrl());
        try {
            update.doc(XContentFactory.jsonBuilder().startObject()
                    .field("in-links", url.getInLinks().toArray()).endObject());
            client.update(update).get();
        } catch (IOException e) {
            System.out.println("Failed to create update for: " + url.getUrl());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        client.close();
    }
}
