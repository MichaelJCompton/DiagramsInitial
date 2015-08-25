package michael.com;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

/**
 * A Zone is a pair (in, K - in) where in \subseteq K.
 *
 * Here with represent the zone with the in set, the set K - in is computed from the parent LabelledDiagram.  This will
 * be ok if we don't need to compute it much, but it may need to be cached if it requires computing often.  However, as
 * parent LabelledDiagram changes the K - in set will also be changed, so it would either need to be notified to each
 * child zone, or if it's only needed for particular operations it should be computed at the start of the operation and
 * kept as valid for only the operation.
 */
public class Zone extends DiagramElement<LabelledDiagram> {

    private Boolean isShaded;
    private AbstractSet<Curve> in;

    Zone () {
        isShaded = false;
        in = new HashSet<Curve>();
    }

    Zone (Boolean shading) {
        isShaded = shading;
        in = new HashSet<Curve>();
    }

    Zone (LabelledDiagram parent, Boolean shading) {
        super(parent);
        isShaded = shading;
        in = new HashSet<Curve>();
    }

    public Boolean isShaded() {
        return isShaded;
    }

    public void setInSet(AbstractCollection<Curve> inSet) {
        in = new HashSet<Curve>(inSet);
    }

    public void addToInSet(Curve c) {
        in.add(c);
    }

    public void removeFromInSet(Curve c) {
        in.remove(c);
    }

    // Don't think spiders are children of their zones.  They can live in many zones.
    // But we may need to collect the spiders that live in particular zones.  At the moment the diagrams (/spiders)
    // give this the other way around.
    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }


}
