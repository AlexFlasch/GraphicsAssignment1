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

    public int windowWidth = 1000;
    public int windowHeight = 1000;
    public final int viewportWidth = windowWidth / 2;
    public final int viewportHeight = windowHeight / 2;

    private GLU glu;

    public Color bg = Color.decode("#2C3E50");
    public ArrayList<Color> palette;

    public final static int NUM = 3000;

    public double x[];
    public double y[];

    public MyKaleidoscope() {
        palette = new ArrayList<>();
        palette.add(Color.decode("#22A7F0"));
        palette.add(Color.decode("#2ECC71"));
        palette.add(Color.decode("#F27935"));
        palette.add(Color.decode("#8E44AD"));
        palette.add(Color.decode("#D64541"));
    }

    public static void main(String[] args) {
        GLJPanel canvas = new GLJPanel();

        MyKaleidoscope kaleidoscope = new MyKaleidoscope();
        canvas.addGLEventListener(kaleidoscope);
        JFrame frame = new JFrame("MyKaleidoscope");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        x = new double[NUM];
        y = new double[NUM];

        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-1.0, 1.0, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // clear all pixels
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        float[] bgArr = getGLColorArray(bg);
        float r = bgArr[0], g = bgArr[1], b = bgArr[2];
        gl.glClearColor(r, g, b, 1.0f);

        // quadrant 1
        gl.glViewport(windowWidth / 2, windowHeight / 2, viewportWidth, viewportHeight);

        // quadrant 2
        gl.glViewport(0, windowHeight / 2, viewportWidth, viewportHeight);

        // quadrant 3
        gl.glViewport(0, 0, viewportWidth, viewportHeight);

        // quadrant 4
        gl.glViewport(windowWidth / 2, 0, viewportWidth, viewportHeight);
    }


    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    public float[] getRandomPaletteColorArray() {
        Random r = new Random();
        int random = r.nextInt(palette.size() + 1);

        Color c = palette.get(random);
        float[] arr = new float[3];

        arr[0] = (float) (c.getRed() / 255.0);
        arr[1] = (float) (c.getGreen() / 255.0);
        arr[2] = (float) (c.getBlue() / 255.0);

        return arr;
    }

    public float[] getGLColorArray(Color c) {
        float[] arr = new float[3];

        arr[0] = (float) (c.getRed() / 255.0);
        arr[1] = (float) (c.getGreen() / 255.0);
        arr[2] = (float) (c.getBlue() / 255.0);

        return arr;
    }


    public void genie(GL2 gl) {
        double fact, fact_now, fact7, fact8;
        double size = 0.3;

        fact = (8 * 2 * Math.PI) / NUM;
        for(int i = 0; i < NUM; i++) {
            fact_now = fact * i;
            fact7 = fact_now * 7;
            fact8 = fact_now * 8;
            x[i] = size * (Math.cos(fact_now) + Math.sin(fact8));
            y[i] = size * (2.0 * Math.sin(fact_now) + Math.sin(fact7));
        }

        gl.glBegin(GL2.GL_LINE_LOOP);
        for(int i = 0; i < NUM; i++) {
            gl.glVertex2d(x[i], y[i]);
        }
        gl.glEnd();
    }
}
