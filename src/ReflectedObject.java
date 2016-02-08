import java.util.ArrayList;

public class ReflectedObject {

    // a ReflectedObject takes into account the 0,0 of a viewport,
    // assuming the viewport is square, and they are all the same size
    // using these assumptions, we can translate the vertices for this object
    // into drawable objects in each of the quadrants

    public ArrayList<Vertex> vertices;

    public ReflectedObject(ArrayList<Vertex> verts) {
        vertices = verts;
    }

    public ArrayList<Vertex> getVerticesForQuadrant(int quadrant) {
        ArrayList<Vertex> translatedVerts = new ArrayList<>();

        switch(quadrant) {
            case 1:
                // calculate vertices for quadrant 1
                break;

            case 2:
                // calculate vertices for quadrant 2
                break;

            case 3:
                // calculate vertices for quadrant 3
                break;

            case 4:
                // calculate vertices for quadrant 4
                break;

            default:
                throw new Error("Uhhh that's not supposed to happen.");
        }

        return translatedVerts;
    }
}