package pt.unl.fct.civitas.data.model;



public class VertexData implements Comparable<VertexData> {
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

    // if s1 > s2, it returns positive number
    // if s1 < s2, it returns negative number
    // if s1 == s2, it returns 0
    @Override
    public int compareTo(VertexData otherVertex) {
        return Integer.parseInt(this.id) - Integer.parseInt(otherVertex.id);
    }
}
