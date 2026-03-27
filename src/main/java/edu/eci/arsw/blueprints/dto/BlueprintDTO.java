package edu.eci.arsw.blueprints.dto;

import java.util.List;


public class BlueprintDTO {
    private String author;
    private String name;
    private List<PointDTO> points;


    public BlueprintDTO() {}

    public BlueprintDTO( String author, String name, List<PointDTO> points) {
        this.author = author;
        this.name = name;
        this.points = points;
    }




    public String getAuthor() { return author; }


    public void setAuthor(String author) { this.author = author; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<PointDTO> getPoints() { return points; }


    public void setPoints(List<PointDTO> points) { this.points = points; }
}
