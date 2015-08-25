package michael.com;

import java.util.AbstractCollection;


// TODO : overall things that might need to be added to the code base
//
// - Lambda functions to map to literals, etc in OWL
// - various constraints and checks from the syntax/semantics to keep everything consistent
//   * constraints on diagrams (build and test these as diagrams are created)
// - checking of OWL compatibility of labels
// - handling in - K sets



/**
 * Created by Michael on 4/08/2015.
 */
public abstract class DiagramElement<ParentType extends DiagramElement> {

    // I think each element should have but one parent?  otherwise some sort of list or set is required.
    // Zones can be inside multiple curves and Spiders in multiple zones, but their parents are the labelled diagram.
    private ParentType myParent;

    private String label;

    private String uniqueID;

    private static final String idPrefix = "element";
    private static final IDGenerator id_Gen = new IDGenerator();

    DiagramElement() {
        initialise();
    }

    DiagramElement(String label) {
        initialise();
        setLabel(label);
    }

    DiagramElement(ParentType parent) {
        initialise();
        setParent(parent);
    }

    DiagramElement(String label, ParentType parent) {
        initialise();
        setParent(parent);
        setLabel(label);
    }

    private void initialise() {
        uniqueID = id_Gen.getID(idPrefix);
        label = "";
    }

    public ParentType parent () {
        return myParent;
    }

    // Just want to ensure that the parents are of the correct type.  But I don't think I can test with reflection
    // because it's GWT.  But is there are nicer way than this?
    // - Could us .getClass().getName() as runtime typechecking?
    public void setParent(ParentType newParent) {
        myParent = newParent;
    }



    abstract public AbstractCollection<DiagramElement> children ();



    // Most DiagramElements need a name.
    // In the syntax/semantics document this is provided by labelling functions.
    // For the implementation, this seems more naturally implemented by giving each component a label (optionally
    // with labelling functions provided by the diagrams to mimic the document syntax).

    public void setLabel(String newLabel) {
        label = newLabel;
    }

    public String getLabel() {
        return label;
    }

    public Boolean isUnLabelled() {
        return label.equals("");
    }


    // Also a unique id
    // For zones, unlabelled curves and spiders, diagrams, anything before it gets a label, etc.

    public String id() {
        return uniqueID;
    }
}
