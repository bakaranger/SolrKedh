package de.tu_bs.cs.rose.kedh;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class SolrConnector {

	private final String url;
	private final SolrClient client;

	public SolrConnector(final String url) {
		this.url = url;
		this.client = new HttpSolrClient.Builder(url).build();
	}

	public void index() {

	}
}
