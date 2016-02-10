import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;

import java.util.ArrayList;

public class ReflectedObject implements Cloneable {

    // a ReflectedObject takes into account the 0,0 of a viewport,
    // assuming the viewport is square, and they are all the same size
    // using these assumptions, we can translate the vertices for this object
    // into drawable objects in each of the quadrants

    public ArrayList<Vertex> vertices;
    public KShape shapeType;
    public float[] colorRGB;

    public ReflectedObject(ArrayList<Vertex> verts, KShape shape, float[] color) {
        this.vertices = verts;
        shapeType = shape;
        colorRGB = color;
    }

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

    public void draw(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

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
                gl.glVertex2d(vert.x, vert.y);
            }
            gl.glEnd();
        }
    }

    public KShape getShapeType() {
        return shapeType;
    }

    public float[] getColorRGB() {
        return colorRGB;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }
}