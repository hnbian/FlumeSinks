package elasticsearch.client;


import com.google.common.annotations.VisibleForTesting;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.event.EventHelper;
import org.apache.flume.sink.elasticsearch.ElasticSearchEventSerializer;
import org.apache.flume.sink.elasticsearch.ElasticSearchIndexRequestBuilderFactory;
import org.apache.flume.sink.elasticsearch.IndexNameBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.flume.sink.elasticsearch.ElasticSearchSinkConstants.DEFAULT_PORT;

public class ElasticSearchJsonTransportClient implements ElasticSearchClient {
    public static final Logger logger = LoggerFactory
            .getLogger(ElasticSearchJsonTransportClient.class);

    private InetSocketTransportAddress[] serverAddresses;
    private ElasticSearchEventSerializer serializer;
    private ElasticSearchIndexRequestBuilderFactory indexRequestBuilderFactory;
    private BulkRequestBuilder bulkRequestBuilder;

    private Client client;

    @VisibleForTesting
    InetSocketTransportAddress[] getServerAddresses() {
        return serverAddresses;
    }

    @VisibleForTesting
    void setBulkRequestBuilder(BulkRequestBuilder bulkRequestBuilder) {
        this.bulkRequestBuilder = bulkRequestBuilder;
    }

    /**
     * Transport client for external cluster
     *
     * @param hostNames
     * @param clusterName
     * @param serializer
     */
    public ElasticSearchJsonTransportClient(String[] hostNames, String clusterName,
                                            ElasticSearchEventSerializer serializer) {
        configureHostnames(hostNames);
        this.serializer = serializer;
        openClient(clusterName);
    }

    public ElasticSearchJsonTransportClient(String[] hostNames, String clusterName,
                                            ElasticSearchIndexRequestBuilderFactory indexBuilder) {
        configureHostnames(hostNames);
        this.indexRequestBuilderFactory = indexBuilder;
        logger.info("ElasticSearchJsonTransportClient 实例化");
        openClient(clusterName);
    }

    /**
     * Local transport client only for testing
     *
     * @param indexBuilderFactory
     */
    public ElasticSearchJsonTransportClient(ElasticSearchIndexRequestBuilderFactory indexBuilderFactory) {
        this.indexRequestBuilderFactory = indexBuilderFactory;
        openLocalDiscoveryClient();
    }

    /**
     * Local transport client only for testing
     *
     * @param serializer
     */
    public ElasticSearchJsonTransportClient(ElasticSearchEventSerializer serializer) {
        this.serializer = serializer;
        openLocalDiscoveryClient();
    }

    /**
     * Used for testing
     *
     * @param client
     *    ElasticSearch Client
     * @param serializer
     *    Event Serializer
     */
    public ElasticSearchJsonTransportClient(Client client,
                                            ElasticSearchEventSerializer serializer) {
        this.client = client;
        this.serializer = serializer;
    }

    /**
     * 测试时使用
     */
    public ElasticSearchJsonTransportClient(Client client,
                                            ElasticSearchIndexRequestBuilderFactory requestBuilderFactory)
            throws IOException {
        this.client = client;
        requestBuilderFactory.createIndexRequest(client, null, null, null);
    }

    private void configureHostnames(String[] hostNames) {
        logger.warn(Arrays.toString(hostNames));
        serverAddresses = new InetSocketTransportAddress[hostNames.length];
        for (int i = 0; i < hostNames.length; i++) {
            String[] hostPort = hostNames[i].trim().split(":");
            String host = hostPort[0].trim();
            int port = hostPort.length == 2 ? Integer.parseInt(hostPort[1].trim())
                    : DEFAULT_PORT;
            serverAddresses[i] = new InetSocketTransportAddress(host, port);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
        client = null;
    }

    @Override
    /**
     * 将新的event 添加到bulk
     */
    public void addEvent(Event event, IndexNameBuilder indexNameBuilder,
                         String indexType, long ttlMs) throws Exception {
        if (bulkRequestBuilder == null) {
            bulkRequestBuilder = client.prepareBulk();
        }
        logger.info("es Event: " + EventHelper.dumpEvent(event, 160));
        IndexRequestBuilder indexRequestBuilder = null;
        logger.info("serializer == null:"+String.valueOf(serializer == null));
        logger.info("serializer"+serializer.getClass());
        logger.info("serializer"+serializer.toString());
        if (indexRequestBuilderFactory == null) {
            indexRequestBuilder = client
                    .prepareIndex(indexNameBuilder.getIndexName(event), indexType)
                    .setSource(serializer.getContentBuilder(event).bytes());
        } else {
            indexRequestBuilder = indexRequestBuilderFactory.createIndexRequest(
                    client, indexNameBuilder.getIndexPrefix(event), indexType, event);
        }

        if (ttlMs > 0) {
            indexRequestBuilder.setTTL(ttlMs);
        }
        bulkRequestBuilder.add(indexRequestBuilder);
    }

    @Override
    public void execute() throws Exception {
        try {
            BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                throw new EventDeliveryException(bulkResponse.buildFailureMessage());
            }
        } finally {
            bulkRequestBuilder = client.prepareBulk();
        }
    }

    /**
     * Open client to elaticsearch cluster
     *
     * @param clusterName
     */
    private void openClient(String clusterName) {
        logger.info("Using ElasticSearch hostnames: {} ",
                Arrays.toString(serverAddresses));
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName).build();

        TransportClient transportClient = new TransportClient(settings);
        for (InetSocketTransportAddress host : serverAddresses) {
            transportClient.addTransportAddress(host);
        }
        if (client != null) {
            client.close();
        }
        client = transportClient;
    }

    /*
     * FOR TESTING ONLY...
     *
     * Opens a local discovery node for talking to an elasticsearch server running
     * in the same JVM
     */
    private void openLocalDiscoveryClient() {
        logger.info("Using ElasticSearch AutoDiscovery mode");
        Node node = NodeBuilder.nodeBuilder().client(true).local(true).node();
        if (client != null) {
            client.close();
        }
        client = node.client();
    }

    @Override
    public void configure(Context context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
