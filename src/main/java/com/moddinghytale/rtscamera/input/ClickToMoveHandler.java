package com.moddinghytale.rtscamera.input;

import hytale.server.world.World;
import hytale.server.world.entity.player.Player;
import hytale.shared.math.Vector3d;
import hytale.shared.math.Vector3f;
import hytale.shared.math.Vector3i;
import hytale.store.Store;
import hytale.store.entity.EntityStore;
import hytale.store.world.entity.Teleport;

public class ClickToMoveHandler {

    public void movePlayerTo(Player player, Vector3i target) {
        World world = player.getWorld();
        if (world == null) return;

        world.execute(() -> {
            if (player.getReference() == null) return;

            Store<EntityStore> store = player.getReference().getStore();
            Teleport teleport = Teleport.createForPlayer(
                    world,
                    new Vector3d(target.x + 0.5, target.y + 1, target.z + 0.5),
                    new Vector3f(0, 0, 0)
            );
            store.addComponent(player.getReference(), Teleport.getComponentType(), teleport);
        });
    }
}
