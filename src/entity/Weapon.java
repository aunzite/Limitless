package entity;

import java.util.ArrayList;

public class Weapon {
    // Attributes
    private String name;       // Name of the weapon (e.g., "Steel Sword")
    private int damage;        // Damage value dealt to enemies
    private double weight;     // Weight of the weapon for balance or stamina use
    private String type;       // Type of weapon (e.g., sword, axe, bow)
    private ArrayList<String> attackHistory; // Stores the attack log for this weapon

    // Constructor
    public Weapon(String name, int damage, double weight, String type) {
        this.name = name;
        this.damage = damage;
        this.weight = weight;
        this.type = type;
        this.attackHistory = new ArrayList<>();
    }
    
    // Add a new attack to the weapon's history
    public void addAttack(String target) {
        attackHistory.add(target);
    }
  
    // Get all attack history
    public ArrayList<String> getAttackHistory() {
        return attackHistory;
    }
  
    // Get weapon name
    public String getName() {
        return name;
    }

    // Get weapon damage
    public int getDamage() {
        return damage;
    }

    // Get weapon weight
    public double getWeight() {
        return weight;
    }

    // Get weapon type
    public String getType() {
        return type;
    }

    // Set weapon name
    public void setName(String name) {
        this.name = name;
    }

    // Set weapon damage
    public void setDamage(int damage) {
        this.damage = damage;
    }

    // Set weapon weight
    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Set weapon type
    public void setType(String type) {
        this.type = type;
    }

    // Get string representation of the weapon
    @Override
    public String toString() {
        return name + " (Damage: " + damage + ", Type: " + type + ")";
    }
} 