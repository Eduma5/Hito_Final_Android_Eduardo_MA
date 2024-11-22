// Character.java
package com.example.tapsoul;

public class Character {
    private String name;
    private int level;
    private int experience;

    public Character(String name) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
    }

    public void gainExperience(int points) {
        this.experience += points;
        if (this.experience >= 100) {
            this.level++;
            this.experience = 0;
        }
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }
}