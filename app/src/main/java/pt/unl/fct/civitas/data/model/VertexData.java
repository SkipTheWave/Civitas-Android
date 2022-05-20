package pt.unl.fct.civitas.data.model;



public class VertexData {
    public String terrainId;
    public String id;
    public String latitude;
    public String longitude;

    public VertexData(){

    }
    public VertexData(String terrainId, String id, String latitude, String longitude){
        this.terrainId = terrainId;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;

    }

}
