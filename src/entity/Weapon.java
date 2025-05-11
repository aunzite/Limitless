package entity;

// Weapon class (Ahmed)
public class Weapon {

    // Attributes
    public String name;
    public int damage;
    public double weight;
    public String type;

    // Constructor
    public Weapon(String name, int damage, double weight, String type) {
        this.name = name;
        this.damage = damage;
        this.weight = weight;
        this.type = type;
    }

    // Accessor methods
    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }
}
