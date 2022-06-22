package pt.unl.fct.civitas.data.model;


import java.util.List;

public class TerrainInfo {
    public String terrainId;
    public double area;
    public String approved;
    public List<VertexData> vertices;

    public TerrainInfo() { }

    public TerrainInfo(String terrainId, double area, String approved, List<VertexData> vertices) {
        this.terrainId = terrainId;
        this.area = area;
        this.approved = approved;
        this.vertices = vertices;
    }
}
