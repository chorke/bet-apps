/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

/**
 *
 * @author Chorke
 */
public abstract class Yield {
    public static enum BetPossibility {Home, Guest, Favorit, Looser, Tie};
    
    protected YieldProperties properties;

    public Yield() {
        properties = new YieldProperties();
    }

    public YieldProperties getProperties() {
        return properties;
    }

    public void setProperties(YieldProperties properties) {
        if(properties == null){
            this.properties = new YieldProperties();
        } else {
            this.properties = properties;
        }
    }
}
