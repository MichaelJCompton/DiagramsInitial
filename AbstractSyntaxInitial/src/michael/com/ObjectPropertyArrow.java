package michael.com;

/**
 * Created by Michael on 4/08/2015.
 */
public class ObjectPropertyArrow extends AbstractArrow {


    ObjectPropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target) {
        super(source, target);
    }

    ObjectPropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                        String label) {
        super(source, target, label);
    }

    ObjectPropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                        String label, AbstractDiagram parent) {
        super(source, target, label, parent);
    }
}
