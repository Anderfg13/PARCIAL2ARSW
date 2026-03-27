package edu.eci.arsw.blueprints.dto;


public class PointDTO {
    private int x;
    private int y;

    public PointDTO() {}

    public PointDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
