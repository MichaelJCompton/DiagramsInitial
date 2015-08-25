package michael.com;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Spider
 *
 * The implementation (different to the syntax/semantics document) mantains the tau and eta functions.
 *
 * For eta this must be altered everytime either the spider is moved, or curves containing the spider are moved and
 * thus new zones (in, K - in) sets are created that it lives in.
 *
 * For tau this must be a reflexive, symmetric relation, which makes adding and removing things a little complicated.
 * For example if s1 and s2 are already joined and s3 is to be added, it requires synchronising the lists for s1, s2,
 * s3 and any other spider joined by any of them.  Similarly removing the line the diagram between s2 and s3 requires
 * all the joined spiders to be updated to the new diagram.  Seems that the best way for this is to just have the
 * functions for adding and removing, with spiders maintaining the invariant of the reflexive, symmetric relation and
 * the diagram traces round the joining lines after additions and removals to call the add and deletes.
 *
 * The context of the Spider determines if it should be an individual or a literal and what the type of that literal
 * should be.
 *
 * However, maybe the Spider should have functions to validate names to ensure they are correct for the implied type?
 */
public class Spider extends DiagramArrowSourceOrTarget {

    private AbstractSet<Zone> eta;
    private AbstractSet<Spider> equalSpiders;   // shared by all spiders in this equivalence


    public Spider () {
        initialiseSpider();
    }

    public Spider(String label) {
        super(label);
        initialiseSpider();
    }

    public Spider(LabelledDiagram parent) {
        super(parent);
        initialiseSpider();
    }

    public Spider(String label, LabelledDiagram parent) {
        super(label, parent);
        initialiseSpider();
    }

    private void initialiseSpider() {
        reSetZones();
        resetTau();
    }


    // For diagrams, Spiders must be in a zone, they don't exist unattached - unenforced.
    public Boolean isInSomeZone () {
        return (eta.size() > 0);
    }


    /* ---- eta ---- */

    public void reSetZones() {
        setZones(new HashSet<Zone>());
    }

    public void setZones(AbstractCollection<Zone> zones) {
        eta = new HashSet<Zone>(zones);
    }

    public void addToZone(Zone z) {
        eta_fn().add(z);
    }

    public void addAllToZones(AbstractCollection<Zone> zones) {
        eta.addAll(zones);
    }

    public void removeFromZone(Zone z) {
        eta_fn().remove(z);
    }

    public Boolean isInZone(Zone z) {
        return eta_fn().contains(z);
    }

    public AbstractSet<Zone> eta_fn() {
        return eta;
    }

    public AbstractSet<Zone> getZones() {
        return eta_fn();
    }


    /* ---- tau ---- */

    public void resetTau() {
        equalSpiders = new HashSet<Spider>();
        equalSpiders.add(this);              // reflexive
    }

    public void setTau(AbstractSet<Spider> newReation) {
        equalSpiders = newReation;          // shared by all spiders in this equivalence
    }

    public void joinTau(Spider other) {

        // the relation should be shared by all spiders in each equivalence, so just union
        AbstractSet<Spider> result = new HashSet<Spider>(tau());
        result.addAll(other.tau());

        // now propagate to everything in both equivalences
        for(Spider s : result) {
            s.setTau(result);
        }
    }

    public AbstractSet<Spider> tau() {
        return equalSpiders;
    }

    public Boolean tau_fn(Spider other) {
        return equalSpiders.contains(other);
    }

    public Boolean isEqualToSpider(Spider other) {
        return tau_fn(other);
    }


}
