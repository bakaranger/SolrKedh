package de.tu_bs.cs.rose.kedh;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
                final StringBuilder bookContent = new StringBuilder();
                for (final File page : dir.toFile().listFiles()) {
                    final String pageContent = new String(Files.readAllBytes(page.toPath()));
                    bookContent.append(pageContent);
                }
                doc.addField("_text_", bookContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            documents.add(doc);
        });
        solr.index(documents);
    }
    
    private static BookMetadata metadata(final String documentID) {
        final String url = "http://gei-digital.gei.de/viewer/oai/?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + documentID;
        try {
            final Document doc = Jsoup.connect(url).get();
            final Element metadata = doc.children().get(0).child(2).getElementsByTag("metadata").get(0);
            String title = "";
            final Elements title_ = metadata.getElementsByTag("dc:title");
            if (title_ != null) {
                title = title_.text();
            }
            
            String creator = "";
            final Elements creator_ = metadata.getElementsByTag("dc:creator");
            if (creator_ != null) {
                creator = creator_.text();
            }
            
            String publisher = "";
            final Elements publisher_ = metadata.getElementsByTag("dc:publisher");
            if (publisher_ != null) {
                publisher = publisher_.text();
            }
            
            String date = "";
            final Elements date_ = metadata.getElementsByTag("dc:date");
            if (date_ != null) {
                date = date_.text();
            }
            
            String source = "";
            final Elements source_ = metadata.getElementsByTag("dc:source");
            if (source_ != null) {
                source = source_.text();
            }
            
            String rights = "";
            final Elements rights_ = metadata.getElementsByTag("dc:rights");
            if (rights_ != null) {
                rights = rights_.text();
            }
            return new BookMetadata(documentID, title, creator, publisher, date, source, rights);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(final String[] args) throws SolrServerException, IOException {
        BasicConfigurator.configure();
        
        final String url = "http://localhost:8983/solr/kedh";
        final String dataDirectory = "/media/rose/Medien/Studium/Master/Information Discovery/wdk-partial-dump";
        
        final SolrConnector solr = new SolrConnector(url);
        
        // delete everything from database
//        solr.getClient().deleteByQuery("*");

//        submissionOne(solr, dataDirectory);
        
        final int maxResults = 20;
        final SolrQuery query = new SolrQuery();
        query.setQuery("Europa");
        query.setRows(maxResults);
        final QueryResponse response = solr.getClient().query(query);
        final SolrDocumentList list = response.getResults();
        list.forEach(doc -> {
            System.out.println(metadata(doc.getFieldValue("id").toString()));
        });
    }
}
