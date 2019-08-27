package com.bakkenbaeck.poddy.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Rss {

    @Attribute
    String version;

    @Element
    Channel channel;


    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "RSS{" +
                "version='" + version + '\'' +
                ", channel=" + channel +
                '}';
    }
}
