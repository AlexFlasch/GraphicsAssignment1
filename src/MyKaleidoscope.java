import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/*
 *   Some extra features added to this program:
 *       * A color palette: each shape will choose a color randomly from the palette
 *       * Random shapes, there are a few things about these:
 *           * Random size
 *           * Random vertices position
 *           * Random color (as stated earlier)
 *           * Random shape type (triangle, circle, rectangle, epicycloid)
 *
 *   I chose an epicycloid for my parameterized curve shape. The equation I found for generating
 *   one of these can be found here: https://elepa.files.wordpress.com/2013/11/fifty-famous-curves.pdf
 *   on page 17 (it is shape #16). Unfortunately my rendered version doesn't look quite like the picture,
 *   but I actually enjoy the look of it currently and so I kept it.
 */

public class MyKaleidoscope implements GLEventListener {

    public int windowWidth = 800;
    public int windowHeight = 800;

    public int viewportsX;
    public int viewportsY;

    private GLUT glut;
    private GLU glu;
    private GL2 gl;

    public Color bg = Color.decode("#2C3E50");
    public ArrayList<Color> palette;

    public ArrayList<ReflectedObject> reflectedObjects = new ArrayList<>();

    public final int NUM = 3000;
    public final double DEG2RAD = 3.14159/180;

    /**
     * Constructor: doesn't do a whole ton aside from define the palette.
     */
    public MyKaleidoscope() {
        palette = new ArrayList<>();
        // take hex values and turn them into java Color objects :)
        palette.add(Color.decode("#22A7F0"));
        palette.add(Color.decode("#2ECC71"));
        palette.add(Color.decode("#F27935"));
        palette.add(Color.decode("#8E44AD"));
        palette.add(Color.decode("#D64541"));
        palette.add(Color.decode("#F7CA18"));
        palette.add(Color.decode("#F62459"));
    }

    /**
     * Main method which gets everything set up for rendering.
     *
     * @param args Accepts 2 parameters:
     *             first defines the amount of viewports in the x direction
     *             second defines the amount of viewports in the y direction
     */
    public static void main(String[] args) {
        GLJPanel canvas = new GLJPanel();
        MyKaleidoscope kaleidoscope = new MyKaleidoscope();

        int arg1 = 3;
        int arg2 = 3;

        if(args.length != 0) {
            arg1 = Integer.parseInt(args[0]);
            arg2 = Integer.parseInt(args[1]);
        }

        kaleidoscope.viewportsX = arg1;
        kaleidoscope.viewportsY = arg2;

        canvas.addGLEventListener(kaleidoscope);
        JFrame frame = new JFrame("MyKaleidoscope");
        frame.setSize(kaleidoscope.windowWidth, kaleidoscope.windowHeight);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    /**
     *  init sets a few things up. Among many things, it adds antialiasing for pretty looking shapes,
     *  sets the background color, calculates what the width and height of the viewports should be,
     *  and lastly sets the gluOrtho2D.
     *
     * @param drawable JOGL drawable object
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();

        // make things pretty and antialias everything :)
        if (gl.isExtensionAvailable("GL_ARB_multisample")) {
            gl.glEnable(GL.GL_MULTISAMPLE);
        }

        float[] bgArr = getGLColorArray(bg);
        float r = bgArr[0], g = bgArr[1], b = bgArr[2];
        gl.glClearColor(r, g, b, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glFlush();

        int viewportWidth = windowWidth / viewportsX;
        int viewportHeight = windowHeight / viewportsY;

        glu.gluOrtho2D(-200, 200, -200, 200);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    /**
     * Runs the methods necessary to draw everything to the screen/viewports!
     *
     * @param glAutoDrawable JOGL GLAutoDrawable object
     */
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        gl = glAutoDrawable.getGL().getGL2();

        drawShapes(gl);

        gl.glFlush();

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {

    }

    /**
     * This method will take a Java Color object and transform it into an array of 3 floats that
     * can be used in glColor3f.
     *
     * @param c The Java Color object to transform into a float array suitable for glColor3f.
     * @return an array of 3 floats. The first being R, second is G, third is B all out of 1 for use in glColor3f.
     */
    public float[] getGLColorArray(Color c) {
        float[] arr = new float[3];

        arr[0] = (float) (c.getRed() / 255.0);
        arr[1] = (float) (c.getGreen() / 255.0);
        arr[2] = (float) (c.getBlue() / 255.0);

        return arr;
    }

    /**
     * This method creates the required amount of viewports and generates the shapes to be drawn in a viewport,
     * draws those shapes, and then generates an entire new set of shapes for the next viewport.
     *
     * @param gl JOGL GL2 object
     */
    public void drawShapes(GL2 gl) {
        int viewportWidth = windowWidth / viewportsX;
        int viewportHeight = windowHeight / viewportsY;

        // create proper amount of viewports
        for(int i = 0; i < viewportsY; i++) {
            for(int j = 0; j < viewportsX; j++) {
                int viewportOffsetX = j * viewportWidth;
                int viewportOffsetY = i * viewportHeight;

                gl.glViewport(viewportOffsetX, viewportOffsetY, viewportWidth, viewportHeight);
                generateShapes();

                // draw shapes for current viewport
                for (ReflectedObject ro : reflectedObjects) {
                    ro.draw(gl);
                }

                gl.glLineWidth(10.0f);
                gl.glColor3f(1.0f, 1.0f, 1.0f);

                gl.glBegin(GL.GL_LINES);
                gl.glVertex2i(0, -200);
                gl.glVertex2i(0, 200);
                gl.glEnd();

                gl.glBegin(GL.GL_LINES);
                gl.glVertex2i(-200, 0);
                gl.glVertex2i(200, 0);
                gl.glEnd();

                // clear list so its different for the next viewport
                reflectedObjects.clear();
            }
        }
    }

    /**
     * Generates the vertices used to draw an epicycloid using a parameterized curve and the parameters passed into it.
     *
     * @param size This variable determines the size of the entire shape being drawn.
     * @param a This is (supposed to be) the radius of the inner circle in which the outer circle rotates around.
     * @param b This is (supposed to be) the radius of the outer circle that rotates around the inner circle.
     * @param startX This determines the x point on the screen that the vertices are generated at.
     * @param startY This determines the y point on the screen that the vertices are generated at.
     * @return A list of Vertex objects (Made myself, mostly just container objects) that is used to draw in OpenGL.
     */
    public ArrayList<Vertex> epicycloid(int size, double a, double b, int startX, int startY) {
        final int POINTS = 3000;

        ArrayList<Vertex> vertices = new ArrayList<>();

        for(int i = 0; i < (100 * Math.PI); i++) {
            double x = startX + (size * ((a + b) * Math.cos(i) - b * Math.cos((a / b + 1) * i)));
            double y = startY + (size * ((a + b) * Math.sin(i) - b * Math.sin((a / b + 1) * i)));

            vertices.add(new Vertex(x, y));
        }

        return vertices;
    }

    /**
     * Generates the vertices used to draw a circle using a parameterized curve and a triangle fan to make a filled in circle.
     *
     * @param size This variable determines the size of the circle.
     * @param startX This determines the x point on the screen that the vertices are generated at.
     * @param startY This determines the y point on the screen that the vertices are generated at.
     * @return A list of Vertex objects that is used to tell OpenGL how to draw the circle
     */
    public ArrayList<Vertex> circle(int size, int startX, int startY) {
        ArrayList<Vertex> vertices = new ArrayList<>();

        for(int i = 0; i < NUM; i++) {
            double degInRad = i * DEG2RAD;

            double x = startX + Math.cos(degInRad) * (size * 2);
            double y = startY + Math.sin(degInRad) * (size * 2);

            vertices.add(new Vertex(x, y));
        }

        return vertices;
    }

    /**
     * This method takes a width and a height and generates a rectangle of that width and height near
     * the specified x and y points
     *
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param startX The x location of the bottom left vertex.
     * @param startY The y location of the bottom left vertex.
     * @return A list of Vertex objects that tells OpenGL how to draw the rectangle.
     */
    public ArrayList<Vertex> rect(int width, int height, int startX, int startY) {
        ArrayList<Vertex> vertices = new ArrayList<>();

        vertices.add(new Vertex(startX, startY)); // bottom left
        vertices.add(new Vertex(startX + width, startY)); // bottom right
        vertices.add(new Vertex(startX + width, startY + height)); // top right
        vertices.add(new Vertex(startX, startY + height)); // top left

        return vertices;
    }

    /**
     * Grabs a random color from the palette List.
     *
     * @return An array of 3 floats for R, G, and B, respectively.
     */
    public float[] getRandomPaletteColorArray() {
        Random r = new Random();
        int rand = r.nextInt(palette.size());

        return getGLColorArray(palette.get(rand));
    }

    /**
     * This method will generate a predetermined amount of shapes for each viewport.
     * The general algorithm for generation is as follows:
     *      1) Randomly select 1 of 4 types of shapes
     *      2) Randomly generate a size variable somewhere between 5 and 20
     *      3) Depending on the shape:
     *          a) Triangles: just generate 3 random vertices within the bounds of a quadrant, choose random color
     *          b) Circles: call upon the circle method to generate the vertices for you, choose random color
     *          c) Rectangles: call upon the rect method to generate the vertices for you, choose random color
     *          d) Epicycloid: call upon the epicycloid method to generate the vertices for you, choose random color
     *      4) Add the newly generated shape to the list of shapes to be drawn.
     */
    public void generateShapes() {
        Random r = new Random();

        for(int shapeNum = 0; shapeNum < 100; shapeNum++) {
            int shape = r.nextInt(4);
            int size = r.nextInt(15) + 5;

            float[] color = getRandomPaletteColorArray();

            ArrayList<Vertex> vertices = new ArrayList<>();

            ReflectedObject temp;

            int quadrantWidth = 100;
            int quadrantHeight = 100;

            switch(shape) {
                case 0:
                    // generate triangle vertices
                    for(int i = 0; i < 3; i++) {
                        int x = r.nextInt(quadrantWidth);
                        int y = r.nextInt(quadrantHeight);

                        vertices.add(new Vertex(x, y));
                    }

                    temp = new ReflectedObject(vertices, KShape.TRIANGLE, color);
                    reflectedObjects.add(temp);
                    break;

                case 1:
                    // generate circle vertices
                    int startingX = r.nextInt(quadrantWidth);
                    int startingY = r.nextInt(quadrantHeight);

                    vertices = circle(size, startingX, startingY);

                    temp = new ReflectedObject(vertices, KShape.CIRCLE, color);
                    reflectedObjects.add(temp);
                    break;

                case 2:
                    // generate quad vertices
                    int width = r.nextInt(200) + 1;
                    int height = r.nextInt(200) + 1;
                    startingX = r.nextInt(quadrantWidth);
                    startingY = r.nextInt(quadrantHeight);
                    vertices = rect(width, height, startingX, startingY);

                    temp = new ReflectedObject(vertices, KShape.QUAD, color);
                    reflectedObjects.add(temp);
                    break;

                case 3:
                    // add butterfly to ReflectedObjects list
                    startingX = r.nextInt(quadrantWidth);
                    startingY = r.nextInt(quadrantHeight);
                    double a = 5.0/3.0;
                    double b = 7.0/6.0;
                    vertices = epicycloid(size, a, b, startingX, startingY);

                    temp = new ReflectedObject(vertices, KShape.EPICYCLOID, color);
                    reflectedObjects.add(temp);
                    break;

                default:
                    throw new Error("Uhh... This shouldn't happen.");
            }
        }
    }
}
