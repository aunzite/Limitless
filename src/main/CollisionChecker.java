/////////////////////////////////////////////////////////////////////////////
// Limitless
// CollisionChecker.java
// 
// Description: Handles collision detection in the game. This class:
// - Manages tile-based collision detection (Ajmal)
// - Calculates entity hitbox positions (Ajmal)
// - Processes directional collision checks (Ajmal)
// - Updates entity collision states (Ajmal)
// - Handles world boundary collision (Ajmal)
/////////////////////////////////////////////////////////////////////////////
package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;
    
    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.playerHitbox.x;
        int entityRightWorldX = entity.worldX + entity.playerHitbox.x + entity.playerHitbox.width - 1;
        int entityTopWorldY = entity.worldY + entity.playerHitbox.y;
        int entityBotWorldY = entity.worldY + entity.playerHitbox.y + entity.playerHitbox.height - 1;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBotRow = entityBotWorldY/gp.tileSize;

        int tileNum1, tileNum2;

        // Check world boundaries
        if (entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol ||
            entityTopRow < 0 || entityBotRow >= gp.maxWorldRow) {
            entity.collisionOn = true;
            return;
        }

        switch(entity.direction) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
                if (entityTopRow < 0) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) p.setLastCollisionTile(entityLeftCol, entityTopRow);
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) {
                        p.setLastCollisionTile(entityLeftCol, entityTopRow);
                        p.setLastCollisionTile(entityRightCol, entityTopRow);
                    }
                }
            }
            case "down" -> {
                entityBotRow = (entityBotWorldY + entity.speed)/gp.tileSize;
                if (entityBotRow >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) p.setLastCollisionTile(entityLeftCol, entityBotRow);
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBotRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBotRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) {
                        p.setLastCollisionTile(entityLeftCol, entityBotRow);
                        p.setLastCollisionTile(entityRightCol, entityBotRow);
                    }
                }
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
                if (entityLeftCol < 0) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) p.setLastCollisionTile(entityLeftCol, entityTopRow);
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBotRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) {
                        p.setLastCollisionTile(entityLeftCol, entityTopRow);
                        p.setLastCollisionTile(entityLeftCol, entityBotRow);
                    }
                }
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
                if (entityRightCol >= gp.maxWorldCol) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) p.setLastCollisionTile(entityRightCol, entityTopRow);
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBotRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    if (entity instanceof entity.Player p) {
                        p.setLastCollisionTile(entityRightCol, entityTopRow);
                        p.setLastCollisionTile(entityRightCol, entityBotRow);
                    }
                }
            }
        }
    }

    // Check collision between two entities
    public void checkEntity(Entity entity1, Entity entity2) {
        // Get entity1's hitbox coordinates
        int entity1Left = entity1.worldX + entity1.playerHitbox.x;
        int entity1Right = entity1Left + entity1.playerHitbox.width;
        int entity1Top = entity1.worldY + entity1.playerHitbox.y;
        int entity1Bottom = entity1Top + entity1.playerHitbox.height;

        // Get entity2's hitbox coordinates
        int entity2Left = entity2.worldX + entity2.playerHitbox.x;
        int entity2Right = entity2Left + entity2.playerHitbox.width;
        int entity2Top = entity2.worldY + entity2.playerHitbox.y;
        int entity2Bottom = entity2Top + entity2.playerHitbox.height;

        // Check for collision
        if (entity1Right > entity2Left && entity1Left < entity2Right &&
            entity1Bottom > entity2Top && entity1Top < entity2Bottom) {
            
            // Determine collision direction
            int overlapX = Math.min(entity1Right - entity2Left, entity2Right - entity1Left);
            int overlapY = Math.min(entity1Bottom - entity2Top, entity2Bottom - entity1Top);
            
            if (overlapX < overlapY) {
                // Horizontal collision
                if (entity1.worldX < entity2.worldX) {
                    entity1.collisionOn = true;
                } else {
                    entity2.collisionOn = true;
                }
            } else {
                // Vertical collision
                if (entity1.worldY < entity2.worldY) {
                    entity1.collisionOn = true;
                } else {
                    entity2.collisionOn = true;
                }
            }
        }
    }

    // Check if two entities are colliding
    public boolean isColliding(Entity entity1, Entity entity2) {
        // Get entity1's hitbox coordinates
        int entity1Left = entity1.worldX + entity1.playerHitbox.x;
        int entity1Right = entity1Left + entity1.playerHitbox.width;
        int entity1Top = entity1.worldY + entity1.playerHitbox.y;
        int entity1Bottom = entity1Top + entity1.playerHitbox.height;

        // Get entity2's hitbox coordinates
        int entity2Left = entity2.worldX + entity2.playerHitbox.x;
        int entity2Right = entity2Left + entity2.playerHitbox.width;
        int entity2Top = entity2.worldY + entity2.playerHitbox.y;
        int entity2Bottom = entity2Top + entity2.playerHitbox.height;

        // Check for collision
        return entity1Right > entity2Left && entity1Left < entity2Right &&
               entity1Bottom > entity2Top && entity1Top < entity2Bottom;
    }
}
