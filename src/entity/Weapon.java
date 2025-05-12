package entity;

/////////////////////////////////////////////////////////////////////////////
// Limitless
// Weapon.java
// 
// Description: Defines weapons that can be used by the player or other entities. 
// - Stores weapon stats such as name, damage, weight, and type (Ahmed)
// - Provides accessors and mutators for managing weapon attributes (Ahmed)
// - Includes toString method for clean text representation (Ahmed)
/////////////////////////////////////////////////////////////////////////////

// Weapon class used to define various weapons in the game
public class Weapon {

    // Attributes (Ahmed)
    public String name;       // Name of the weapon (e.g., "Steel Sword")
    public int damage;        // Damage value dealt to enemies
    public double weight;     // Weight of the weapon for balance or stamina use
    public String type;       // Type of weapon (e.g., sword, axe, bow)

    // Constructor (Ahmed)
    // Initializes all weapon stats when object is created
    public Weapon(String name, int damage, double weight, String type) {
        this.name = name;
        this.damage = damage;
        this.weight = weight;
        this.type = type;
    }

    // Accessors (Ahmed)
    // Returns the weapon's name
    public String getName() {
        return name;
    }

    // Returns the weapon's damage value
    public int getDamage() {
        return damage;
    }

    // Mutators (Ahmed)
    // Updates the weapon's name
    public void setName(String name) {
        this.name = name;
    }

    // Updates the weapon's damage value
    public void setDamage(int damage) {
        this.damage = damage;
    }

    // toString override (Ahmed)
    // Returns a readable string representing the weapon
    public String toString() {
        return name + " (Damage: " + damage + ", Type: " + type + ")";
    }
}
