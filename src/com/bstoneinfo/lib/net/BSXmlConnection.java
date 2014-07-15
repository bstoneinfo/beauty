package com.bstoneinfo.lib.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class BSXmlConnection extends BSHttpUrlConnection {

    public interface BSXmlConnectionListener {
        void finished(Document xmlDoc);

        void failed(Exception exception);
    }

    public BSXmlConnection(String url) {
        super(url);
    }

    public void start(final BSXmlConnectionListener listener) {
        start(new BSHttpUrlConnectionListener() {

            @Override
            public void finished(byte[] response) {
                if (listener != null) {
                    try {
                        InputStream is = new ByteArrayInputStream(response);
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                        listener.finished(doc);
                    } catch (Exception e) {
                        failed(e);
                    }
                }
            }

            @Override
            public void failed(Exception exception) {
                if (listener != null) {
                    listener.failed(exception);
                }
            }
        });
    }

}
