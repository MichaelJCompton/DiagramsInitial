package michael.com;

import java.util.AbstractCollection;
import java.util.HashSet;

/**
 * Created by Michael on 4/08/2015.
 */
public abstract class AbstractArrow extends DiagramElement<AbstractDiagram> {

    // TODO : need to add the lambda functions once this code is linked up with WebProt

    private DiagramArrowSourceOrTarget source;
    private DiagramArrowSourceOrTarget target;

    protected enum CardinalityConstraint {NONE, EQ, LEQ, GEQ}
    private CardinalityConstraint cardinalityConstraint;
    private Integer cardinality;

    AbstractArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target) {
        this.source = source;
        this.target = target;
        cardinalityConstraint = CardinalityConstraint.NONE;
    }

    AbstractArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                  String label) {
        super(label);
        this.source = source;
        this.target = target;
        cardinalityConstraint = CardinalityConstraint.NONE;
    }

    AbstractArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                  String label, AbstractDiagram parent) {
        super(label, parent);
        this.source = source;
        this.target = target;
        cardinalityConstraint = CardinalityConstraint.NONE;
    }

    public void setCardinalityConstraint(CardinalityConstraint constraint, Integer cardinality) {
        cardinalityConstraint = constraint;
        this.cardinality = cardinality;
    }

    public Boolean hasCardinalityConstraint() {
        return cardinalityConstraint == CardinalityConstraint.NONE;
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }


}
