
package chorke.proprietary.bet.apps.core.match;

import chorke.proprietary.bet.apps.StaticConstants;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Chorke
 */
public class MatchProperties {
    
    private String country;
    private String league;
    private Calendar date;

    public MatchProperties() {
        date = new GregorianCalendar();
    }
    
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date.setTimeInMillis(date.getTimeInMillis());
    }

    @Override
    public String toString() {
        return date.get(Calendar.DAY_OF_MONTH) 
                + ". "
                + date.getDisplayName(Calendar.MONTH, Calendar.LONG, StaticConstants.SVK_LOCALE)
                + " " 
                + date.get(Calendar.YEAR);
    }
}
