package de.tu_bs.cs.rose.kedh;

/**
 * @author rose
 * @version 0.1
 */
public class BookMetadata {
    
    private final String id;
    private final String title;
    private final String creator;
    private final String publisher;
    private final String date;
    private final String source;
    private final String rights;
    
    public BookMetadata(final String id, final String title, final String creator, final String publisher, final String date, final String source, final String rights) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.publisher = publisher;
        this.date = date;
        this.source = source;
        this.rights = rights;
    }
    
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getCreator() {
        return creator;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public String getDate() {
        return date;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getRights() {
        return rights;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final BookMetadata that = (BookMetadata) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        int indentLength = "Dublin Core Metadata".length();
        final String[] fields = {"Title", "Author or Creator", "Publisher", "Date", "Source", "Rights Management"};
        return String.format("Dublin Core Metadata: %s%n" +
                        "%" + indentLength + "s: %s%n" + // title
                        "%" + indentLength + "s: %s%n" + // creator
                        "%" + indentLength + "s: %s%n" + // publisher
                        "%" + indentLength + "s: %s%n" + // date
                        "%" + indentLength + "s: %s%n" + // source
                        "%" + indentLength + "s: %s%n",  // rights
                id, fields[0], title, fields[1], creator, fields[2], publisher, fields[3], date, fields[4], source, fields[5], rights);
    }
}
