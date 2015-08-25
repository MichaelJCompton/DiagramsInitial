package michael.com;

/**
 * Created by Michael on 4/08/2015.
 */
public class DatatypePropertyArrow extends AbstractArrow {


    DatatypePropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target) {
        super(source, target);
    }

    DatatypePropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                          String label) {
        super(source, target, label);
    }

    DatatypePropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                          String label, AbstractDiagram parent) {
        super(source, target, label, parent);
    }
}
