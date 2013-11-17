/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import java.util.List;

/**
 *
 * @author Chorke
 */
public class CumulativeYieldProperties extends YieldProperties{

    public CumulativeYieldProperties() {
        super();
    }
    
    public CumulativeYieldProperties(List<Double> scale, String betCompany) {
        super(scale, betCompany);
    }

    public CumulativeYieldProperties(List<Double> scale, String betCompany, double doubleNumber, String note) {
        super(scale, betCompany, doubleNumber, note);
    }

}
