
package chorke.bet.apps.io;

import chorke.bet.apps.core.Tuple;
import chorke.bet.apps.core.bets.Bet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Udržuje informácie, pre načítanie požadovaných stávok. 
 * Vytvára aj {@link PreparedStatement} pre načítanie týchto stávok z DB podľa
 * aktuálne nastavených parametrov. 
 * @author Chorke
 */
public class LoadProperties extends Properties{
    
    public static final int START_DATE = 1;
    public static final int END_DATE = 2;
    public static final int BET_COMPANY = 3;
    public static final int COUNTRY_LEAGUE = 4;
    public static final int BET_CLASS = 5;
    
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
        addToSet(BET_COMPANY, betCompany);
    }
    
    /**
     * Nájde prvok s kľúčom key a pridá do tejto množiny prvok value.
     * Ak neexistuje kľúč, je vytvorená nová množina.
     * 
     * @param key
     * @param value 
     */
    @SuppressWarnings("unchecked")
    private void addToSet(int key, Object value){
        Set<Object> set;
        if(containsKey(key)){
            Object o = get(key);
            if(o instanceof Set){
                set = (Set<Object>)get(key);
            } else {
                throw new IllegalArgumentException("Object in map with key " + key + " is not set");
            }
        } else {
            set = new HashSet<>();
            put(key, set);
        }
        set.add(value);
    }
    
    /**
     * Vráti požadované stávkové spoločnosti.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBetCompanies(){
        return (Set<String>)get(BET_COMPANY);
    }
    
    /**
     * Pridá vyžadovanú ligu a krajinu ligy. Ligy sú ukladané v množine.
     * Ukladaný formát je (krajina, liga).
     * @param league  
     * @see Set
     */
    public void addLeague(String country, String league){
        addToSet(COUNTRY_LEAGUE, new Tuple<>(country, league));
    }
    
    /**
     * Vráti požadované ligy. Ukladaný formát je (krajina, liga).
     * @return 
     */
    @SuppressWarnings("unchecked")
    public Set<Tuple<String, String>> getLeagues(){
        return (Set<Tuple<String, String>>)get(COUNTRY_LEAGUE);
    }
    
    /**
     * Pridá požadovaný typ stávok. Triedy sú ukladané v množine.
     * @param <T>
     * @param clazz 
     */
    public <T extends Bet> void addBetClass(Class<T> clazz){
        addToSet(BET_CLASS, clazz);
    }
    
    /**
     * Vráti požadované typy stávok.
     * @return 
     */
    @SuppressWarnings("unchecked")
    public <T extends Bet> Set<Class<T>> getBetClasses(){
        return (Set<Class<T>>)get(BET_CLASS);
    }
    
    /**
     * Vytvorí podmienku na match id. Neobsahuje žiadne zátvorky ani samotné
     * WHERE kľúčové slovo. Nikdy nie je {@code null}.
     * @param ids
     * @return 
     */
    public String getWhereClauseWithIds(){
        StringBuilder sb = new StringBuilder("");
        String where = getWhereClauseForMatches();
        if(where.isEmpty()){
            return sb.toString();
        }
        sb.append("matchid IN (SELECT id FROM matches").append(where).append(")");
        return sb.toString();
    }
    
    /**
     * Pripraví dotaz pre score.
     * @param con
     * @param whereClauseWithIds
     * @return
     * @throws SQLException 
     */
    public PreparedStatement prepareScoresStatement(Connection con) throws SQLException{
        StringBuilder sb =  new StringBuilder("SELECT * FROM scores");
        String whereClauseWithIds = getWhereClauseWithIds();
        if(!whereClauseWithIds.isEmpty()){
            PreparedStatement ps = con.prepareStatement(
                sb.append(" WHERE ").append(whereClauseWithIds).toString(), 
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            fillPreparedStatementForMatchesWhereClause(ps, 1);
            return ps;
        }
        return con.prepareStatement(sb.toString(), 
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    
    /**
     * Bet - konkrétny typ stávky.
     * @param betCompanies
     * @param bet
     * @return
     * @throws SQLException 
     */
    public PreparedStatement prepareBetStatement(
            Connection con,  String bet) throws SQLException{
        StringBuilder out = new StringBuilder("SELECT * FROM ").append(bet);
        Set<String> betCompanies = getBetCompanies();
        String whereClauseWithIds = getWhereClauseWithIds();
        if((betCompanies == null || betCompanies.isEmpty())
                && (whereClauseWithIds.isEmpty())){
            return con.prepareStatement(out.toString());
        }
        out.append(" WHERE ");
        boolean needAnd = false;
        if(betCompanies != null && !betCompanies.isEmpty()){
            needAnd = true;
            for(int i = 0; i < betCompanies.size(); i++){
                out.append("betcompany LIKE ? OR ");
            }
            out.delete(out.length() - 4, out.length());
        }
        if(whereClauseWithIds != null && !whereClauseWithIds.isEmpty()){
            if(needAnd){
                out.append(" AND ");
            }
            out.append("(").append(whereClauseWithIds).append(")");
        }
        PreparedStatement ps = con.prepareStatement(out.toString());
        int i = 1;
        if(betCompanies != null && !betCompanies.isEmpty()){
            for(String betCompany : betCompanies){
                ps.setString(i++, betCompany);
            }
        }
        fillPreparedStatementForMatchesWhereClause(ps, i);
        return ps;
    }
    
    /**
     * Vytvorí dotaz pre zápas.
     * @param con
     * @param start
     * @param end
     * @param leagues
     * @return
     * @throws SQLException 
     */
    public PreparedStatement prepareMatchStatement(Connection con) throws SQLException{
        StringBuilder out = new StringBuilder("SELECT * FROM matches");
        String where = getWhereClauseForMatches();
        if(where.isEmpty()){
            return con.prepareStatement(out.toString());
        }
        out.append(where);
        PreparedStatement ps = con.prepareStatement(out.toString());
        fillPreparedStatementForMatchesWhereClause(ps, 1);
        return ps;
    }
    
    /**
     * Vyplní where klauzulu. Úzko súvisí s metódou {@link #getWhereClauseForMatches()}.
     * Vyplňovať začne od indexu startIdx.
     * 
     * @param ps
     * @param startIdx počiatočný index
     * @throws SQLException 
     */
    private void fillPreparedStatementForMatchesWhereClause(PreparedStatement ps, int startIdx)
            throws SQLException{
        Calendar start = getStartDate();
        Calendar end = getEndDate();
        Set<Tuple<String, String>> leagues = getLeagues();
        int i = startIdx;
        if(start != null){
            ps.setTimestamp(i, new Timestamp(start.getTimeInMillis()));
            i++;
        }
        if(end != null){
            ps.setTimestamp(i, new Timestamp(end.getTimeInMillis()));
            i++;
        }
        if(leagues != null && !leagues.isEmpty()){
            for(Tuple<String, String> tp : leagues){
                if(tp.first != null && !tp.first.isEmpty()){
                    ps.setString(i, tp.first);
                    i++;
                }
                if(tp.second != null && !tp.second.isEmpty()){
                    ps.setString(i, tp.second);
                    i++;
                }
            }
        }
    }
    
    /**
     * Vytvorí where časť pre SELECT * FROM matches dotaz. Nikdy nie je {@code null}.
     * @param start
     * @param end
     * @param leagues
     * @return 
     */
    private String getWhereClauseForMatches(){
        StringBuilder out = new StringBuilder("");
        Calendar start = getStartDate();
        Calendar end = getEndDate();
        Set<Tuple<String, String>> leagues = getLeagues();
        boolean hasSomeLeagues = isAnythingForCountryLeaguePart(leagues);
        if(start == null && end == null && !hasSomeLeagues){
            return out.toString();
        }
        out.append(" WHERE ");
        boolean needAnd = false;
        if(start != null){
            out.append("matchdate >= ?");
            needAnd = true;
        }
        if(end != null){
            if(needAnd){ out.append(" AND "); }
            out.append("matchdate <= ?");
            needAnd = true;
        }
        if(hasSomeLeagues){
            if(needAnd){
                out.append(" AND ");
            }
            out.append("(");
            for(Tuple<String, String> tp : leagues){
                String clwp = getCountryLeagueWherePart(tp);
                if(!clwp.isEmpty()){
                    out.append("(").append(clwp).append(") OR ");
                }
            }
            out.delete(out.length() - 4, out.length());
            out.append(")");
        }
        return out.toString();
    }
    
    /**
     * Vráti true, ak existuje nejaká liga alebo krajina, ktorá mý byť zahrnutá
     * vo WHERE časti.
     * 
     * @param leagues
     * @return 
     */
    private boolean isAnythingForCountryLeaguePart(Set<Tuple<String, String>> leagues){
        if(leagues == null || leagues.isEmpty()){
            return false;
        }
        for(Tuple<String, String> tp : leagues){
            if((tp.first != null && !tp.first.isEmpty())
                    || tp.second != null && !tp.second.isEmpty()){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vytvorí časť v klauzule pre ligu a krajinu. Ak je niektorá z nich 
     * prázdna alebo null, tak je v klauzule vynechaná (prípadne obe).
     * 
     * @param tuple formát (liga, krajina)
     * @return 
     */
    private String getCountryLeagueWherePart(Tuple<String, String> tuple){
        String league = tuple.second;
        String country = tuple.first;
        StringBuilder out = new StringBuilder("");
        boolean needAnd = false;
        if(country != null && !country.isEmpty()){
            out.append("country LIKE ?");
            needAnd = true;
        }
        if(league != null && !league.isEmpty()){
            if(needAnd){
                out.append(" AND ");
            }
            out.append("league LIKE ?");
        }
        return out.toString();
    }
    
    /**
     * Ak nie je požadovaná daná trieda, je ps zatvorený a vráti null. 
     * Inak vráti pôvodný ps.
     * @param <T>
     * @param clazz
     * @param ps
     * @return
     * @throws SQLException 
     */
    public <T extends Bet> PreparedStatement checkClass(Class<T> clazz, PreparedStatement ps)
            throws SQLException{
        Set classes = getBetClasses();
        if(classes != null){
            if(!classes.contains(clazz)){
                ps.close();
                return null;
            }
        }
        return ps;
    }
}
