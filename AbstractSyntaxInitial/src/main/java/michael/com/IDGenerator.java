package michael.com;

/**
 * A class to help generate a unique id for each diagram element as it is created.
 *
 * Want to keep it simple and readable, so things like UID are no good.
 *
 * This one just gives each a number as a string.  Abstracted from Diagram element in case I want to make it different
 * some day.
 */
public class IDGenerator {

    private Integer currentID;

    IDGenerator() {
        currentID = 0;
    }

    // no need for synchronized cause it's GWT
    public String getID() {
        String result = currentID.toString();
        currentID++;
        return result;
    }

    public String getID(String prefix) {
        return prefix + getID();
    }
}
