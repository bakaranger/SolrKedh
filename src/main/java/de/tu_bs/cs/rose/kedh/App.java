package de.tu_bs.cs.rose.kedh;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(final String[] args) {
		BasicConfigurator.configure();
		logger.info("Hello World");

		final String url = "http://localhost:8983/solr/kedh";

		final SolrConnector solr = new SolrConnector(url);
		solr.index();
	}
}
