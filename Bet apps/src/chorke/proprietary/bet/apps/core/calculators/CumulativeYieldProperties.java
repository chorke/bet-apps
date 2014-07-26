
package chorke.proprietary.bet.apps.core.calculators;

import java.math.BigDecimal;
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
    
    public CumulativeYieldProperties(List<BigDecimal> scale, String betCompany) {
        super(scale, betCompany);
    }

    public CumulativeYieldProperties(List<BigDecimal> scale, String betCompany, Map<String, Object> properties) {
        super(scale, betCompany, properties);
    }

}
