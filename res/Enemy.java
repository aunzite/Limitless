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

    // Methods
    public void attack() {
        System.out.println(name + " attacks for " + damage + " damage!");
    }

    public void takeDamage(int dmg) {
        health = health - dmg;
    }

    public int getHealth() {
        return health;
    }
}