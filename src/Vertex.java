/**
 * Mostly a container object to keep all my variables in a nice compact format.
 */
public class Vertex {
    public double x;
    public double y;

    /**
     * Constructor. Nothing special here.
     *
     * @param x The value for the x field.
     * @param y The value for the y field.
     */
    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the x field.
     *
     * @param x The new value for the x class field.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y field.
     *
     * @param y The new value for the y class field.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the x field.
     *
     * @return The x class field.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y field.
     *
     * @return The y class field.
     */
    public double getY() {
        return y;
    }
}
