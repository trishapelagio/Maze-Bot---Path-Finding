public class Node {
    private Maze value;
    private Node parent;

    //Bind content of c to the node
    public Node(Maze c) {
        this.value = c;
    }

    //Returns the content bound to the node
    public Maze getContent() {
        return value;
    }

    //Set content bound to the note
    public void setContent(Maze value) {
        this.value = value;
    }

    //Return father Node from current Node
    public Node getParent() {
        return parent;
    }

    //Set parent of this Node
    public void setParent(Node parent) {
        this.parent = parent;
    }

    //Returns true if Node has parent
    public boolean hasParent() {
        return this.parent != null;
    }

    //Returns link parent -> child in a String
    public String printParent() {
        return this.value.toString() + " <- " + this.parent.value.toString();
    }

    @Override
    public String toString() {
        return this.getContent().toString();
    }

}
