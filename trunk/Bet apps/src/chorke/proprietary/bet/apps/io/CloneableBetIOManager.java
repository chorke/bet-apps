
package chorke.proprietary.bet.apps.io;

/**
 * Podporuje klonovanie pre účely multivláknového spracovania tak, aby každé vlákno
 * mohlo pracovať s vlastným manažérom pre urýchlenie a vyhnutie sa vytváranniu 
 * vláknovo bezpečných (synchronizovaných) metód.
 * 
 * @author Chorke
 */
public interface CloneableBetIOManager extends BetIOManager, Cloneable{
    
    /**
     * Vytvorí klon tohto managera.
     * @return 
     */
    CloneableBetIOManager cloneBetIOManager();
}
