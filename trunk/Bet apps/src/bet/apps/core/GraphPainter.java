package bet.apps.core;

import java.util.List;
import javax.swing.JPanel;

public interface GraphPainter {
    
    public JPanel paintGraph(List<PaintableGraph> graph);
}
