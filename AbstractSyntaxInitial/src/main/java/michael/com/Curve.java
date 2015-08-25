package michael.com;

import java.util.AbstractCollection;

/**
 * Created by Michael on 4/08/2015.
 */
public class Curve extends DiagramArrowSourceOrTarget {

    public Curve() {
        super();
    }

    public Curve(LabelledDiagram parent) {
        super(parent);
    }

    public Curve(String label) {
        super(label);
    }

    public Curve(String label, LabelledDiagram parent) {
        super(label, parent);
    }


}
