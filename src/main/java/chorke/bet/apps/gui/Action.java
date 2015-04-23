
package chorke.bet.apps.gui;

/**
 * Trieda, ktorá po zavolaní {@link #doAction()} vykoná jej obsah.
 * @author Chorke
 */
public abstract class Action {

    /**
     * Posledné pridelené ID.
     */
    private static long lastId = 0;
    
    /**
     * Id tejto akcie
     */
    private long id;
    
    /**
     * Vytvorí novú akciu, ktorej pridelí jedinečné ID.
     */
    public Action() {
        id = getNextId();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Action)){
            return false;
        }
        return id == ((Action)obj).id;
    }

    @Override
    public int hashCode() {
        return (int)id;
    }
    
    /**
     * Vracia jedinečné id v rámci všetkých inštancií tejto triedy.
     * @return 
     */
    private static synchronized long getNextId(){
        return ++lastId;
    }
    
    /**
     * Kód, ktorý má byť vykonaný touto akciou.
     */
    public abstract void doAction();
}
