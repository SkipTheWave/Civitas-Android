package pt.unl.fct.civitas.data.model;

import java.util.List;

public class TerrainData {
    public String username;
    public String terrainId;
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
    public String approved;
    public boolean shared;
    public List<VertexData> vertices;

    public TerrainData() {

    }
    public TerrainData(String terrainId, String owner, double area, String county, String section, String article, String name,
                       String description, String coverage, String current, String last, String owners,String approved,List<VertexData> vertices) {
        this.terrainId = terrainId;
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
        this.vertices = vertices;
        this.approved = approved;
    }


    public TerrainData(String terrainId, String owner, double area, String county, String section, String article, String name,
                       String description, String coverage, String current, String last, String owners,String approved, boolean shared, List<VertexData> vertices) {
        this.terrainId = terrainId;
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
        this.vertices = vertices;
        this.approved = approved;
        this.shared = shared;
    }

    public TerrainData(String owner, double area, String county, String section, String article, String name,
                       String description, String coverage, String current, String last, String owners, String approved) {
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
        this.approved = approved;
    }

    public TerrainData(String username, String owner, String terrainId){
        this.username = username;
        this.owner = owner;
        this.terrainId = terrainId;
    }

    public TerrainData(String username, String terrainId, String owner, String name, String description, String coverage,
                       String current, String last, String owners, String approved) {
        this.username = username;
        this.terrainId = terrainId;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.coverage = coverage;
        this.current = current;
        this.last = last;
        this.owners = owners;
        this.approved = approved;
    }
}
