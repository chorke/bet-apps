
package chorke.bet.apps.core;

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
        if(this == obj){
            return true;
        }
        if(obj == null
                || !(obj instanceof Tuple)){
            return false;
        }
        Tuple t = (Tuple) obj;
        
        boolean firstEq = this.first == null ? t.first == null : this.first.equals(t.first);
        boolean secondEq = this.second == null ? t.second == null : this.second.equals(t.second);
        return firstEq && secondEq;
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) 
                + (second == null ? 0 : second.hashCode());
    }
}
