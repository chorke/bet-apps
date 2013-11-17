/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.filters;


/**
 *
 * @author Chorke
 */
public abstract class AbstractBetFilter implements Filter {

    double lowerThreshold;
    double upperThreshold;

    public double getLowerThreshold() {
        return lowerThreshold;
    }

    public double getUpperThreshold() {
        return upperThreshold;
    }

    public void setLowerThreshold(double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    public void setUpperThreshold(double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }
}
