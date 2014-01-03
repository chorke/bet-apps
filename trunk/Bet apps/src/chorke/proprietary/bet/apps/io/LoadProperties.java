
package chorke.proprietary.bet.apps.io;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Chorke
 */
public class LoadProperties extends Properties{
    
    public static final int START_DATE = 1;
    public static final int END_DATE = 2;
    public static final int BET_COMPANY = 3;
    public static final int LEAGUE = 4;
    
    /**
     * Nastaví začiatočný dátum. Ekvivalentné 
     * {@code put(}{@link #START_DATE}{@code ,}{@link Calendar}{@code )}
     * @param start 
     */
    public void addStartDate(Calendar start){
        put(START_DATE, start);
    }
    
    /**
     * Vráti začiatočný dátum. Ekvivalentné 
     * {@code get(}{@link #START_DATE}{@code )}
     */
    
    public Calendar getStartDate(){
        return (Calendar)get(START_DATE);
    }
    
    /**
     * Nastaví koncový dátum. Ekvivalentné 
     * {@code put(}{@link #END_DATE}{@code ,}{@link Calendar}{@code )}
     * @param end 
     */
    
    public void addEndDate(Calendar end){
        put(END_DATE, end);
    }
    
    /**
     * Vráti koncový dátum. Ekvivalentné 
     * {@code get(}{@link #END_DATE}{@code )}
     */
    
    public Calendar getEndDate(){
        return (Calendar)get(END_DATE);
    }
    
    /**
     * Pridá vyžadovanú stávkovú spoločnosť. Spoločnosti sú ukladané v množine.
     * @param betCompany 
     * @see Set
     */
    public void addBetCompany(String betCompany){
        Set set;
        if(contains(BET_COMPANY)){
            set = ((Set)get(BET_COMPANY));
        } else {
            set = new HashSet<>();
            put(BET_COMPANY, set);
        }
        set.add(betCompany);
    }
    
    /**
     * Vráti požadované stávkové spoločnosti.
     */
    public Set<String> getBetCompanies(){
        return (Set<String>)get(BET_COMPANY);
    }
    
    /**
     * Pridá vyžadovanú ligu. Ligy sú ukladané v množine.
     * @param league  
     * @see Set
     */
    public void addLeague(String league){
        Set set;
        if(contains(LEAGUE)){
            set = ((Set)get(LEAGUE));
        } else {
            set = new HashSet<>();
            put(LEAGUE, set);
        }
        set.add(league);
    }
    
    /**
     * Vráti požadované ligy.
     * @return 
     */
    public Set<String> getLeagues(){
        return (Set<String>)get(LEAGUE);
    }
}
