package de.tu_bs.cs.rose.kedh;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public abstract class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static SolrInputDocument buildDocument(final Map<String, String> fields) {
        final SolrInputDocument doc = new SolrInputDocument();
        fields.forEach((name, value) -> doc.addField(name, value));
        return doc;
    }
    
    private static void submissionOne(final SolrConnector solr, final String dataDirectory) throws IOException {
        final Collection<SolrInputDocument> documents = new ArrayList<>();
        Files.list(Paths.get(dataDirectory)).filter(Files::isDirectory).forEach(dir -> {
            final SolrInputDocument doc = new SolrInputDocument();
            final String bookName = dir.getFileName().toString();
            doc.addField("id", bookName);
            try {
                for (final File page : dir.toFile().listFiles()) {
                    final String pageContent = new String(Files.readAllBytes(page.toPath()));
                    final String pageName = page.getName().substring(0, page.getName().lastIndexOf('.'));
                    // store data as text: indexed, tokenized and stored
                    doc.addField("page_" + pageName + "_txt_de", pageContent);
                    
                    // buit-in text field _text_
                    // doc.addField("_text_", pageContent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            documents.add(doc);
        });
        solr.index(documents);
    }
    
    public static void main(final String[] args) throws SolrServerException, IOException {
        BasicConfigurator.configure();
        
        final String url = "http://localhost:8983/solr/kedh";
        final String dataDirectory = "/home/rose/Studium/Master/Information Discovery/wdk-partial-dump";
        
        final SolrConnector solr = new SolrConnector(url);
        
        // delete everything from database
        solr.getClient().deleteByQuery("*");
        
        submissionOne(solr, dataDirectory);
        
    }
}
