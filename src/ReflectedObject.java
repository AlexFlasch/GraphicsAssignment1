import com.jogamp.opengl.GL2;

import java.util.ArrayList;

public class ReflectedObject implements Cloneable {

    // a ReflectedObject takes into account the 0,0 of a viewport,
    // assuming the viewport is square, and they are all the same size
    // using these assumptions, we can translate the vertices for this object
    // into drawable objects in each of the quadrants.

    public ArrayList<Vertex> vertices;
    public KShape shapeType;
    public float[] colorRGB;

    /**
     * Constructor, nothing special here.
     *
     * @param verts The vertices of the shape.
     * @param shape The shapeType of the shape (Triangle, Circle, Rectangle, Epicycloid)
     * @param color A 3 float array for R, G, and B, respectively.
     */
    public ReflectedObject(ArrayList<Vertex> verts, KShape shape, float[] color) {
        this.vertices = verts;
        shapeType = shape;
        colorRGB = color;
    }

    /**
     * Since every shape has vertices with points somewhere between 0 and whatever the width of the quadrant is
     * these points must be translated over to draw into all the quadrants.
     *
     * @param quadrant The quadrant to grab the vertices for (1-4, following the same form as cartesian quadrants).
     * @return A list of vertices that tells OpenGL how to draw the shape in the specified quadrant.
     */
    private ArrayList<Vertex> getVerticesForQuadrant(int quadrant) {
        ArrayList<Vertex> translatedVerts = new ArrayList<>();

        switch(quadrant) {
            case 1:
                // calculate vertices for quadrant 1 (top right)
                for(Vertex vert : vertices) {
                    double translatedX = vert.x;
                    double translatedY = vert.y;

                    translatedVerts.add(new Vertex(translatedX, translatedY));
                }
                break;

            case 2:
                // calculate vertices for quadrant 2 (top left)
                for(Vertex vert : vertices) {
                    double translatedX = -1 * vert.x;
                    double translatedY = vert.y;

                    translatedVerts.add(new Vertex(translatedX, translatedY));
                }
                break;

            case 3:
                // calculate vertices for quadrant 3 (bottom left)
                for(Vertex vert : vertices) {
                    double translatedX = -1 * vert.x;
                    double translatedY = -1 * vert.y;

                    translatedVerts.add(new Vertex(translatedX, translatedY));
                }
                break;

            case 4:
                // calculate vertices for quadrant 4 (bottom right)
                for(Vertex vert : getVertices()) {
                    double translatedX = vert.x;
                    double translatedY = -1 * vert.y;

                    translatedVerts.add(new Vertex(translatedX, translatedY));
                }
                break;

            default:
                throw new Error("Uhhh that's not supposed to happen.");
        }

        return translatedVerts;
    }

    /**
     * This method will draw a shape in each of the 4 quadrants automagically.
     * This allows the shapes to be relatively easy to handle in the sense that having "1 shape" will let
     * you draw all 4 that you really need.
     *
     * @param gl The JOGL GL2 object.
     */
    public void draw(GL2 gl) {
        int glShape;

        switch(shapeType) {
            case TRIANGLE:
                glShape = GL2.GL_TRIANGLES;
                break;
            case CIRCLE:
                glShape = GL2.GL_TRIANGLE_FAN;
                break;
            case QUAD:
                glShape = GL2.GL_QUADS;
                break;
            case EPICYCLOID:
                glShape = GL2.GL_LINE_LOOP;
                break;
            default:
                throw new Error("Uhh... This shouldn't happen.");
        }

        for(int i = 1; i <= 4; i++) {
            ArrayList<Vertex> translatedVerts = getVerticesForQuadrant(i);

            gl.glBegin(glShape);
            gl.glColor3f(colorRGB[0], colorRGB[1], colorRGB[2]);
            for(Vertex vert : translatedVerts) {
                if(vert.x > 200 || vert.x < -200 || vert.y < -200 || vert.y > 200)
                    System.out.println("vert x: " + vert.x + "\nvert y: " + vert.y);
                gl.glVertex2d(vert.x, vert.y);
            }
            gl.glEnd();
        }
    }

    /**
     * Gets the shapeType field.
     *
     * @return The shapeType class field.
     */
    public KShape getShapeType() {
        return shapeType;
    }

    /**
     * Gets the colorRGB field.
     *
     * @return The colorRGB class field.
     */
    public float[] getColorRGB() {
        return colorRGB;
    }

    /**
     * Gets the vertices field.
     *
     * @return The vertices class field.
     */
    public ArrayList<Vertex> getVertices() {
        return vertices;
    }
}