/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Chorke
 */
public class DocumentDownloader {

    public static final int CONNECTION_TRIES = 5;
    
    public Document getDocument(String url){
        Document doc = null;
        for(int i = 1; i <= CONNECTION_TRIES; i++){
            try{
                doc = Jsoup.connect(url).timeout(5_000).get();
                return doc;
            } catch (IOException ex){
                System.err.println("Time out [" + i + "] by connecting: " + url);
                System.err.println(ex);
            }
        }
        return doc;
    }
}
