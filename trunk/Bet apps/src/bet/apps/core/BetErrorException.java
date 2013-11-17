/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.core;

/**
 *
 * @author Chorke
 */
public class BetErrorException extends Exception {

    /**
     * Creates a new instance of
     * <code>BetErrorException</code> without detail message.
     */
    public BetErrorException() {
    }

    /**
     * Constructs an instance of
     * <code>BetErrorException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BetErrorException(String msg) {
        super(msg);
    }
}
