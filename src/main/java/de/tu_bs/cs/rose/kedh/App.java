package de.tu_bs.cs.rose.kedh;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class App {

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private static SolrInputDocument buildDocument(final Map<String, String> fields) {
    final SolrInputDocument doc = new SolrInputDocument();
    fields.forEach((name, value) -> doc.addField(name, value));
    return doc;
  }

  public static void main(final String[] args) throws SolrServerException, IOException {
    BasicConfigurator.configure();
    logger.info("Hello World");

    final String url = "http://localhost:8983/solr/kedh";

    final SolrConnector solr = new SolrConnector(url);

    final Map<String, String> fields = new HashMap<>();
    fields.put("id", "42");
    fields.put("name", "Jewgeni Rose");
    final SolrInputDocument doc = buildDocument(fields);

    solr.index(doc);
  }
}
