package de.tu_bs.cs.rose.kedh;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

public class SolrConnector {
    
    private final String url;
    private final SolrClient client;
    
    public SolrConnector(final String url) {
        this.url = url;
        this.client = new HttpSolrClient.Builder(url).build();
    }
    
    public String getURL() {
        return url;
    }
    
    public SolrClient getClient() {
        return client;
    }
    
    public void index(final SolrInputDocument document) throws SolrServerException, IOException {
        client.add(document);
        client.commit();
    }
}
