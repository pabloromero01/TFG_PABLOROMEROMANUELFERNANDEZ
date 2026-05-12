package com.tfg.futbolstats.model;

public class Player {

    private int id;
    private String name;
    private String firstname;
    private String lastname;
    private int age;
    private String nationality;
    private String position;
    private String photo;
    private PlayerStats stats;

    // Campos extra de TheSportsDB
    private String teamName;
    private String birthDate;
    private String description;

    public Player() {}

    public Player(int id, String name, int age, String nationality, String position) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.nationality = nationality;
        this.position = position;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public PlayerStats getStats() { return stats; }
    public void setStats(PlayerStats stats) { this.stats = stats; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name + (nationality != null ? " (" + nationality + ")" : "");
    }
}