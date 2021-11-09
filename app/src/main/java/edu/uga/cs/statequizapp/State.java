package edu.uga.cs.statequizapp;

public class State {
    private long id;
    private String name;
    private String capital;
    private String[] cities = new String[2];

    public State(){
        this.id = -1;
        this.name = null;
        this.capital = null;
        this.cities = null;
    }

    public State(String name, String capital, String[] cities){
        this.id=1;
        this.name = name;
        this.capital = capital;
        this.cities = cities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String[] getCities() {
        return cities;
    }

    public void setCities(String[] cities) {
        this.cities = cities;
    }

    public String toString() {
        return id + ": " + name + " " + capital + " " + cities[0] + " " + cities[1];

    }
}
