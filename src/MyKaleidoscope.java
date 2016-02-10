import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MyKaleidoscope implements GLEventListener {

    public static int windowWidth = 800;
    public static int windowHeight = 800;

    public static int viewportsX;
    public static int viewportsY;

    private GLU glu;

    public Color bg = Color.decode("#2C3E50");
    public ArrayList<Color> palette;

    public static ArrayList<ReflectedObject> reflectedObjects = new ArrayList<>();

    public final static int NUM = 3000;
    public final double DEG2RAD = 3.14159/180;

    public MyKaleidoscope() {
        palette = new ArrayList<>();
        // take hex values and turn them into java Color objects :)
        palette.add(Color.decode("#22A7F0"));
        palette.add(Color.decode("#2ECC71"));
        palette.add(Color.decode("#F27935"));
        palette.add(Color.decode("#8E44AD"));
        palette.add(Color.decode("#D64541"));
    }

    public static void main(String[] args) {
        GLJPanel canvas = new GLJPanel();
        MyKaleidoscope kaleidoscope = new MyKaleidoscope();

        int arg1 = 3;
        int arg2 = 3;

        if(args.length != 0) {
            arg1 = Integer.parseInt(args[0]);
            arg2 = Integer.parseInt(args[1]);
        }

        viewportsX = arg1;
        viewportsY = arg2;

        canvas.addGLEventListener(kaleidoscope);
        JFrame frame = new JFrame("MyKaleidoscope");
        frame.setSize(MyKaleidoscope.windowWidth, windowHeight);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();

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

        glu.gluOrtho2D(-viewportWidth / 2, viewportWidth / 2, -viewportHeight / 2, viewportHeight / 2);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        Color lineColor = Color.decode("#F62459");
        float[] lineColorArr = getGLColorArray(lineColor);

        int viewportWidth = windowWidth / viewportsX;
        int viewportHeight = windowHeight / viewportsY;

        // create proper amount of viewports
        for(int i = 0; i < viewportsY; i++) {
            for(int j = 0; j < viewportsX; j++) {
                int viewportOffsetX = j * viewportWidth;
                int viewportOffsetY = i * viewportHeight;

                gl.glViewport(viewportOffsetX, viewportOffsetY, viewportWidth, viewportHeight);
                generateShapes();
            }
        }

        gl.glFlush();

        for (ReflectedObject ro : reflectedObjects) {
            ro.draw(glAutoDrawable);
        }

    }


    @Override
    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {

    }

    public float[] getGLColorArray(Color c) {
        float[] arr = new float[3];

        arr[0] = (float) (c.getRed() / 255.0);
        arr[1] = (float) (c.getGreen() / 255.0);
        arr[2] = (float) (c.getBlue() / 255.0);

        return arr;
    }


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

    public ArrayList<Vertex> rect(int width, int height, int startX, int startY) {
        ArrayList<Vertex> vertices = new ArrayList<>();

        vertices.add(new Vertex(startX, startY)); // bottom left
        vertices.add(new Vertex(startX + width, startY)); // bottom right
        vertices.add(new Vertex(startX + width, startY + height)); // top right
        vertices.add(new Vertex(startX, startY + height)); // top left

        return vertices;
    }

    public float[] getRandomPaletteColorArray() {
        Random r = new Random();
        int rand = r.nextInt(palette.size());

        return getGLColorArray(palette.get(rand));
    }

    public void generateShapes() {
        Random r = new Random();

        for(int shapeNum = 0; shapeNum < 50; shapeNum++) {
            int shape = r.nextInt(4);
            int size = r.nextInt(15) + 5;

            float[] color = getRandomPaletteColorArray();

            ArrayList<Vertex> vertices = new ArrayList<>();

            ReflectedObject temp;

            int quadrantWidth = windowWidth / 2;
            int quadrantHeight = windowHeight / 2;

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
