package pt.unl.fct.civitas.data.model;

public class TerrainData {
    public String owner;
    public double area;
    public String county;
    public String section;
    public String article;
    public String name;
    public String description;
    public String coverage;
    public String current;
    public String last;
    public String owners;

    public TerrainData() {

    }

    public TerrainData(String owner, double area, String county, String section, String article, String name,
                       String description, String coverage, String current, String last, String owners) {
        this.owner = owner;
        this.area = area;
        this.county = county;
        this.section = section;
        this.article = article;
        this.name = name;
        this.description = description;
        this.coverage = coverage;
        this.current = current;
        this.last = last;
        this.owners = owners;
    }
}
