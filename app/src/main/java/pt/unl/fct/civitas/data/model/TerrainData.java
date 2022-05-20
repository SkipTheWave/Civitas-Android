package pt.unl.fct.civitas.data.model;


import java.util.List;

public class TerrainData {
    public String terrainId;
    public List<VertexData> vertexList;

    public TerrainData() { }

    public TerrainData(String terrainId, List<VertexData> vertexList) {
        this.terrainId = terrainId;
        this.vertexList = vertexList;
    }
}
