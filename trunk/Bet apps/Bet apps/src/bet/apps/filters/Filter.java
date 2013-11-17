
package bet.apps.filters;

import bet.apps.core.Match;

public interface Filter {
    
    /**
     * Determines wheter {@code match} pass filters conditions.
     * 
     * @param match match to be determined
     * @return true if match pass filter, false otherwise
     */
    public boolean passFilter(Match match);
}
