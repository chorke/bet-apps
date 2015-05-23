
package chorke.bet.apps.core.httpparsing;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stiahne document z webu.
 * @author Chorke
 */
public class DocumentDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentDownloader.class);
    
    /**
     * Aktuálne 5
     */
    public static final int CONNECTION_TRIES = 5;
    
    private static final int CONNECTION_TIMEOUT = 8_000;
    
    /**
     * Pokúsi sa stiahnuť web stránku. Počet pokusov je {@link #CONNECTION_TRIES}.
     * Time out je 5 000. V prípade núspechu vracia null;
     * @param url
     * @return 
     */
    public Document getDocument(String url){
        for(int i = 1; i <= CONNECTION_TRIES; i++){
            try{
                return Jsoup.connect(url).timeout(CONNECTION_TIMEOUT).get();
            } catch (IOException ex){
                LOG.warn("Time out [{}] by connecting: {}.", i, url);
                LOG.warn("Time out exception.", ex);
            }
        }
        return null;
    }
}
