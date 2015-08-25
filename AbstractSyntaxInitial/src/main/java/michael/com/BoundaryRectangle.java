package michael.com;

import java.util.AbstractCollection;

/**
 *  * may need functions for accessing curves, spiders etc - are these children + separate?
 *
 *
 * Some constraints
 *
 * - The parent of a BoundaryRectangle can really only be a LabelledDiagram (or subclass).
 */
public class BoundaryRectangle extends DiagramArrowSourceOrTarget {

    public BoundaryRectangle() {
        super();
    }

    public BoundaryRectangle(LabelledDiagram parent) {
        super(parent);
    }


    // TODO : Are its children every thing in the labelled diagram?  In anycase that should be the rectangle's
    //      parent, so easy enough to access those things.

}
