package com.moddinghytale.rtscamera.input;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ClickToMoveHandler {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public void movePlayerTo(Player player, Vector3i target) {
        World world = player.getWorld();
        if (world == null) {
            LOGGER.atWarning().log("[RTSCamera:Move] Player world is null, cannot teleport.");
            return;
        }

        double destX = target.x + 0.5;
        double destY = target.y + 1;
        double destZ = target.z + 0.5;
        LOGGER.atInfo().log("[RTSCamera:Move] Teleporting player to (%.1f, %.1f, %.1f)", destX, destY, destZ);

        world.execute(() -> {
            try {
                if (player.getReference() == null) {
                    LOGGER.atWarning().log("[RTSCamera:Move] Player reference is null inside world.execute()");
                    return;
                }

                Store<EntityStore> store = player.getReference().getStore();
                Teleport teleport = Teleport.createForPlayer(
                        world,
                        new Vector3d(destX, destY, destZ),
                        new Vector3f(0, 0, 0)
                );
                store.addComponent(player.getReference(), Teleport.getComponentType(), teleport);
                LOGGER.atInfo().log("[RTSCamera:Move] Teleport component added successfully.");
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log("[RTSCamera:Move] Error during teleport!");
            }
        });
    }
}
