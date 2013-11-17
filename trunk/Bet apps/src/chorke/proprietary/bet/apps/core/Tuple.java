/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core;

/**
 *
 * @author Chorke
 */
public final class Tuple<X, Y> {
    public X first;
    public Y second;

    public Tuple() {
        first = null;
        second = null;
    }

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
        
        if(t.first == null && this.first != null){
            return false;
        }
        if(t.first != null && this.first == null){
            return false;
        }
        if(t.second == null && this.second != null){
            return false;
        }
        if(t.second != null && this.second == null){
            return false;
        }
        
        if(t.first.getClass() != this.first.getClass()
                || t.second.getClass() != this.second.getClass()){
            return false;
        }
        return t.first.equals(this.first) && t.second.equals(this.second);
    }

    @Override
    public int hashCode() {
        return this.first.hashCode() * this.second.hashCode();
    }
    
}
