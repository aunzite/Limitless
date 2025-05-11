public class Enemy {

    // Attributes
    private String name;
    private int health;
    private int damage;

    // Constructor
    public Enemy(String n, int h, int d) {
        name = n;
        health = h;
        damage = d;
    }

    // Method to attack
    public void attack() {
        System.out.println(name + " attacks and deals " + damage + " damage!");
    }

    // Method to take damage
    public void takeDamage(int dmg) {
        health = health - dmg;
        if (health < 0) {
            health = 0;
        }
    }

    // Method to check if enemy is alive
    public boolean isAlive() {
        return health > 0;
    }

    // Getter for health
    public int getHealth() {
        return health;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for damage
    public int getDamage() {
        return damage;
    }

    // Set new damage (example mutator)
    public void setDamage(int newDamage) {
        damage = newDamage;
    }
}
