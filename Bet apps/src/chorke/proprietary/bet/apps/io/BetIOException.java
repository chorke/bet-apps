/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.io;

/**
 *
 * @author Chorke
 */
public class BetIOException extends RuntimeException {

    public BetIOException() {
    }

    public BetIOException(String msg) {
        super(msg);
    }

    public BetIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
