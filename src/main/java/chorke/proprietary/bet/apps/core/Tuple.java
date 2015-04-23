
package chorke.proprietary.bet.apps.core;

/**
 * Trieda, ktorá je reprezentuje dvojicu. 
 * @author Chorke
 */
public final class Tuple<X, Y> {
    public final X first;
    public final Y second;

    /**
     * Vytvorí novú dvojicu.
     * @param first prvý prvok dvojice
     * @param second druhý prvok dvojice.
     */
    public Tuple(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null
                || !(obj instanceof Tuple)){
            return false;
        }
        Tuple t = (Tuple) obj;
        
        if(t.first == null && this.first != null){ return false; }
        if(t.first != null && this.first == null){ return false; }
        if(t.second == null && this.second != null){ return false; }
        if(t.second != null && this.second == null){ return false; }
        if(t.first != null 
            && (t.first.getClass() != this.first.getClass() || !t.first.equals(this.first))
                ){ return false; }
        if(t.second != null 
            && (t.second.getClass() != this.second.getClass() || !t.second.equals(this.second))
                ){ return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) 
                + (second == null ? 0 : second.hashCode());
    }
    
}
