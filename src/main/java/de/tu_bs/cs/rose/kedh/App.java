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

public abstract class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static final String DC = "dc:";
    private static final String STRING_TYPE = "_s";
    private static final String TEXT_TYPE = "_t";
    private static final String TITLE = "title";
    private static final String CREATOR = "creator";
    private static final String PUBLISHER = "publisher";
    private static final String DATE = "date";
    private static final String SOURCE = "source";
    private static final String RIGHTS = "rights";
    
    private static final int NO_LIMITS = -1;
    private static final boolean METADATA = true;
    
    private static void resetDatabase(final SolrConnector solr) {
        // delete everything from database
        try {
            solr.getClient().deleteByQuery("*");
        } catch (final SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void fillDatabase(final SolrConnector solr, final String dataDirectory) throws IOException {
        fillDatabase(solr, dataDirectory, false);
    }
    
    private static void fillDatabase(final SolrConnector solr, final String dataDirectory, boolean withMetadata) throws IOException {
        final Collection<SolrInputDocument> documents = new ArrayList<>();
        Files.list(Paths.get(dataDirectory)).filter(Files::isDirectory).forEach(dir -> {
            final SolrInputDocument doc = new SolrInputDocument();
            final String documentID = dir.getFileName().toString();
            doc.addField("id", documentID);
            try {
                final StringBuilder bookContent = new StringBuilder();
                for (final File page : dir.toFile().listFiles()) {
                    final String pageContent = new String(Files.readAllBytes(page.toPath()));
                    bookContent.append(pageContent);
                }
                doc.addField("_text_", bookContent);
                if (withMetadata) {
                    fillMetadata(doc, documentID);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            documents.add(doc);
        });
        solr.index(documents);
    }
    
    private static void fillMetadata(final SolrInputDocument doc, final String documentID) {
        final BookMetadata metadata = metadata(documentID);
        if (metadata != null) {
            doc.addField(TITLE + STRING_TYPE, metadata.getTitle());
            doc.addField(CREATOR + STRING_TYPE, metadata.getCreator());
            doc.addField(PUBLISHER + STRING_TYPE, metadata.getPublisher());
            doc.addField(DATE + STRING_TYPE, metadata.getDate());
            doc.addField(SOURCE + TEXT_TYPE, metadata.getSource());
            doc.addField(RIGHTS + STRING_TYPE, metadata.getRights());
        }
    }
    
    private static BookMetadata metadata(final String documentID) {
        final String url = "http://gei-digital.gei.de/viewer/oai/?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + documentID;
        try {
            final Document doc = Jsoup.connect(url).get();
            final Element metadata = doc.children().get(0).child(2).getElementsByTag("metadata").get(0);
            String title = "";
            final Elements title_ = metadata.getElementsByTag(DC + TITLE);
            if (title_ != null) {
                title = title_.text();
            }
            
            String creator = "";
            final Elements creator_ = metadata.getElementsByTag(DC + CREATOR);
            if (creator_ != null) {
                creator = creator_.text();
            }
            
            String publisher = "";
            final Elements publisher_ = metadata.getElementsByTag(DC + PUBLISHER);
            if (publisher_ != null) {
                publisher = publisher_.text();
            }
            
            String date = "";
            final Elements date_ = metadata.getElementsByTag(DC + DATE);
            if (date_ != null) {
                date = date_.text();
            }
            
            String source = "";
            final Elements source_ = metadata.getElementsByTag(DC + SOURCE);
            if (source_ != null) {
                source = source_.text();
            }
            
            String rights = "";
            final Elements rights_ = metadata.getElementsByTag(DC + RIGHTS);
            if (rights_ != null) {
                rights = rights_.text();
            }
            return new BookMetadata(documentID, title, creator, publisher, date, source, rights);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static SolrDocumentList query(final SolrConnector solr, final String query, int maxResults) {
        final SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        if (maxResults > 0) { // else no limits
            solrQuery.setRows(maxResults);
        }
        QueryResponse response = null;
        try {
            response = solr.getClient().query(solrQuery);
        } catch (final SolrServerException | IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            return response.getResults();
        }
        return new SolrDocumentList();
    }
    
    private static SolrDocumentList query(final SolrConnector solr, final String query) {
        return query(solr, query, 10);
    }
    
    private static void print(final SolrDocumentList list) {
        print(list, true);
    }
    
    private static void print(final SolrDocumentList list, boolean loadMetadata) {
        if (loadMetadata) {
            list.forEach(doc -> {
                System.out.println(metadata(doc.getFieldValue("id").toString()));
            });
        } else {
            list.forEach(doc -> {
                final Collection<String> fieldNames = doc.getFieldNames();
                fieldNames.forEach(field -> {
                    System.out.println(field + ": " + doc.getFieldValue(field));
                });
                System.out.println();
            });
        }
    }
    
    
    public static void main(final String[] args) throws SolrServerException, IOException {
        BasicConfigurator.configure();
        
        final String url = "http://localhost:8983/solr/kedh";
        final SolrConnector solr = new SolrConnector(url);
        
//        resetDatabase(solr);
        
//        final String dataDirectory = "/home/rose/Studium/Master/Information Discovery/wdk-partial-dump";
//        fillDatabase(solr, dataDirectory, METADATA);


        final SolrDocumentList results = query(solr, "Europa");
        print(results, !METADATA);
    
    }
}
