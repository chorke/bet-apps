/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import java.util.List;
import java.util.Map;

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

    public CumulativeYieldProperties(List<Double> scale, String betCompany, Map<String, Object> propeerties) {
        super(scale, betCompany, propeerties);
    }

}
