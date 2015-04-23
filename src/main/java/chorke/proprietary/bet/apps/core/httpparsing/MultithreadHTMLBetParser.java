
package chorke.proprietary.bet.apps.core.httpparsing;

/**
 * Parser pre sťahovanie stávok. Pre sťahovanie využíva viac vlákien, aby
 * bolo efektívnnejšie.
 * 
 * @author Chorke
 */
public interface MultithreadHTMLBetParser extends HTMLBetParser{
    
    /**
     * Zastaví všetky vlákna, ktoré boli spustené pre sťahovanie stávok a 
     * zastaví aj samotné sťahovanie.
     */
    void stopThreads();
}
