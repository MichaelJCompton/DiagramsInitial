package michael.com;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

/**
 *
 * A LabelledDiagram is a tuple (rect, Sigma, K, Z, Z*, eta, tau, A, lambda_s, lambda_c, lambda_a, lambda_#)
 *
 * where
 *
 * rect     : the boundry rectangle of the diagram
 * Sigma    : finite set of Spider(s)
 * K        : finite set of Curve(s)
 * Z        : set of Zone(s) s.t. z \subseteq {(in, K-in) : in \subseteq K}
 * Z*       : set of shaded Zone(s) s.t. Z* \subseteq Z
 * eta      : function giving the set of Zone(s) a Spider resides in
 * tau      : reflexive, symetric (equality) relation on Sigma
 * A        : finitie multiset of arrows (s,t,o) s.t. s,t : Sigma U K U rect
 * lambda_s : labels Spider(s) as OWL individuals or literals
 * lambda_c : labels Curve(s) as OWL concepts or datatypes
 * lambda_a : labels Arrow(s) as OWL object property expressions
 * lambda_# : labels Arrow(s) with <=, =, =< for OWL cardinality constraints
 *
 * the lambda and eta and tau are implemented in the Spider, Arrow and Curve classes
 *
 * TODO : for the moment the lambda functions aren't implemented until the code is hooked up wth WebProtege
 */
public abstract class LabelledDiagram extends AbstractDiagram {

    // TODO
    // missingZones
    // lambda functions

    // TODO : may also have functions in the class to access the underlying functions for eta and tau and lambdas??
    //      : programatically I don' think it matters as if we have the spider we have access to the function
    //      : but would allow writing expressions like in the syntax ... just seems that implementing it as part of
    //      : spider is simpler than recording some sort of lookup table here.



    private BoundaryRectangle boundryRectangle;
    private AbstractSet<Spider> spiders;
    private AbstractSet<Curve> curves;
    private AbstractSet<Zone> zones;
    private AbstractSet<AbstractArrow> arrows;

    // Shading is part of the zone in the implementation, so it's computed and cached here.
    private AbstractSet<Zone> shadedZones;
    private boolean shadedZonesUpToDate;


    LabelledDiagram () {
        initialiseLabelledDiagram();
    }

    LabelledDiagram(ConceptDiagram parent) {
        super(parent);
        initialiseLabelledDiagram();
    }

    private void initialiseLabelledDiagram() {
        spiders = new HashSet<Spider>();
        curves = new HashSet<Curve>();
        zones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
        shadedZonesUpToDate = false;
        arrows = new HashSet<AbstractArrow>();
    }

    public AbstractSet<Zone> getShadedZones() {
        if (!shadedZonesUpToDate) {
            computeShadedZones();
        }
        return shadedZones;
    }

    private void computeShadedZones() {
        shadedZones = new HashSet<Zone>();
        for(Zone z : zones) {
            if(z.isShaded()) {
                shadedZones.add(z);
            }
        }
        shadedZonesUpToDate = true;
    }

    public AbstractSet<Zone> eta(Spider s) {
        return s.eta_fn();
    }

    public Boolean tau(Spider s1, Spider s2) {
        // should probably only return true if :
        //      s1 is in this diagram
        //      s2 is in this diagram
        //      they are related by their tau relations
        return s1.tau_fn(s2);
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        AbstractSet<DiagramElement> result = new HashSet<DiagramElement>();
        result.add(boundryRectangle);
        result.addAll(spiders);
        result.addAll(curves);
        result.addAll(zones);
        result.addAll(arrows);
        return result;
    }
}
