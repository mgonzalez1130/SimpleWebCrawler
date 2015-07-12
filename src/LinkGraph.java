import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class LinkGraph {

    public static void main(String args[]) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("LinkGraph"));
            writeOutLinks(writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

    }

    @SuppressWarnings("unchecked")
    private static void writeOutLinks(BufferedWriter writer) {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "clarke").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(
                        "10.0.0.12", 9300));

        SearchResponse scrollResp = client.prepareSearch("merged_crawler")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setScroll(new TimeValue(60000))
                .setQuery(QueryBuilders.matchAllQuery()).setSize(100).execute()
                .actionGet();

        System.out.println("Number of hits: "
                + scrollResp.getHits().getTotalHits());

        int counter = 0;
        while (true) {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                String line = hit.getId();
                ArrayList<String> outLinks = (ArrayList<String>) hit
                        .getSource().get("in-links");

                for (String outLink : outLinks) {
                    line = line + "\t" + outLink;
                }
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                counter++;
            }

            System.out.println("Documents written: " + counter);

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(600000)).execute().actionGet();

            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }

        System.out.println("Finished writing)");
        client.close();
    }
}
