package pt.unl.fct.civitas.data.model;

public class shareTerrainInfo {

    public String owner;
    public String[] sharedwith;
    public String terrainId;

    public shareTerrainInfo(){
    }

    public shareTerrainInfo(String owner, String[] sharedwith, String terrainId){
        this.owner = owner;
        this.terrainId = terrainId;
        this.sharedwith = sharedwith;
    }
}
