package com.bakkenbaeck.poddy.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom")
})
@Root(strict = false)
public class Channel {
    @ElementList(entry = "link", inline = true, required = false)
    public List<Link> links;

    @ElementList(name = "item", required = true, inline = true)
    public List<Item> itemList;

    @Element
    public String title;
    @Element
    public String description;
    @Element
    String language;

    @Element(name = "ttl", required = false)
    int ttl;

    @Element(name = "pubDate", required = false)
    String pubDate;

    @Override
    public String toString() {
        return "Channel{" +
                "links=" + links +
                ", itemList=" + itemList +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", ttl=" + ttl +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }

    public static class Link {
        @Attribute(required = false)
        public String href;

        @Attribute(required = false)
        public String rel;

        @Attribute(name = "type", required = false)
        public String contentType;

        @Text(required = false)
        public String link;
    }

    @Root(name = "item", strict = false)
    public static class Item {

        @Path("title")
        @Text(required=false)
        public String title;
        @Element(name = "link", required = false)
        String link;
        @Path("description")
        @Text(required=false)
        public String description;
        @Element(name = "category", required = false)
        String category;
        @Element(name = "comments", required = false)
        String comments;
        @Element(name = "enclosure", required = false)
        public Enclosure enclosure;
        @Element(name = "guid", required = false)
        public String guid;
        @Element(name = "pubDate", required = false)
        public String pubDate;
        @Element(name = "source", required = false)
        String source;
        @Element(name = "duration", required = false)
        public int duration;

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", description='" + description + '\'' +
                    ", category='" + category + '\'' +
                    ", comments='" + comments + '\'' +
                    ", enclosure='" + enclosure + '\'' +
                    ", guid='" + guid + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

    @Root(name = "enclosure", strict = false)
    public static class Enclosure {
        @Attribute(name = "url", required = false)
        String url;
        @Attribute(name = "type", required = false)
        String type;
    }
}
