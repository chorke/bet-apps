/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Stiahne document z webu.
 * @author Chorke
 */
public class DocumentDownloader {

    /**
     * Aktuálne 5
     */
    public static final int CONNECTION_TRIES = 5;
    
    /**
     * Pokúsi sa stiahnuť web stránku. Počet pokusov je {@link #CONNECTION_TRIES}.
     * Time out je 5 000. V prípade núspechu vracia null;
     * @param url
     * @return 
     */
    public Document getDocument(String url){
        for(int i = 1; i <= CONNECTION_TRIES; i++){
            try{
                return Jsoup.connect(url).timeout(5_000).get();
            } catch (IOException ex){
                System.err.println("Time out [" + i + "] by connecting: " + url);
                System.err.println(ex);
            }
        }
        return null;
    }
}