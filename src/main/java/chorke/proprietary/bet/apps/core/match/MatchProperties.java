
package chorke.proprietary.bet.apps.core.match;

import chorke.proprietary.bet.apps.core.CoreUtils;
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

    /**
     * Ak je {@code null}, tak sa nestane nič.
     * @param date 
     */
    public void setDate(Calendar date) {
        if(date != null){
            this.date.setTimeInMillis(date.getTimeInMillis());
        }
    }

    @Override
    public String toString() {
        return date.get(Calendar.DAY_OF_MONTH) 
                + ". "
                + date.getDisplayName(Calendar.MONTH, Calendar.LONG, CoreUtils.SVK_LOCALE)
                + " " 
                + date.get(Calendar.YEAR);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        MatchProperties mp = (MatchProperties)obj;
        return mp.country.equals(this.country)
                && mp.league.equals(this.league)
                && mp.date.getTimeInMillis() == this.date.getTimeInMillis();
    }

    @Override
    public int hashCode() {
        return 31 * (country == null ? 0 : country.hashCode() 
                + league == null ? 0 : league.hashCode()
                + (int)date.getTimeInMillis());
    }
}
