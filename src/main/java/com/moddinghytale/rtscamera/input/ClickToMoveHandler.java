package com.moddinghytale.rtscamera.input;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.moddinghytale.rtscamera.config.PluginConfig;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClickToMoveHandler {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final long MOVE_TICK_MS = 50;

    private final ScheduledExecutorService executor;
    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> activeMoves;
    private final float moveSpeed;
    private final float stopDistance;

    public ClickToMoveHandler(PluginConfig config) {
        this.moveSpeed = config.getRtsMoveSpeed();
        this.stopDistance = config.getRtsStopDistance();
        this.activeMoves = new ConcurrentHashMap<>();
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rtscam-move");
            t.setDaemon(true);
            return t;
        });
    }

    public void shutdown() {
        for (ScheduledFuture<?> future : activeMoves.values()) {
            future.cancel(true);
        }
        activeMoves.clear();
        executor.shutdownNow();
    }

    public void cancelMove(UUID playerId) {
        ScheduledFuture<?> future = activeMoves.remove(playerId);
        if (future != null) {
            future.cancel(true);
        }
    }

    public void movePlayerTo(Player player, PlayerRef playerRef, Vector3i target) {
        World world = player.getWorld();
        if (world == null) {
            LOGGER.atWarning().log("[RTSCamera:Move] Player world is null, cannot move.");
            return;
        }
        if (playerRef == null) {
            LOGGER.atWarning().log("[RTSCamera:Move] PlayerRef is null, cannot move.");
            return;
        }

        UUID playerId = playerRef.getUuid();
        cancelMove(playerId);

        Vector3d targetPos = new Vector3d(target.x + 0.5, target.y + 1.0, target.z + 0.5);
        LOGGER.atInfo().log("[RTSCamera:Move] Moving player toward (%.1f, %.1f, %.1f)", targetPos.x, targetPos.y, targetPos.z);

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                () -> tickMove(world, playerRef, targetPos, playerId),
                0L,
                MOVE_TICK_MS,
                TimeUnit.MILLISECONDS
        );
        activeMoves.put(playerId, future);
    }

    private void tickMove(World world, PlayerRef playerRef, Vector3d targetPos, UUID playerId) {
        if (world == null || playerRef == null || !playerRef.isValid()) {
            cancelMove(playerId);
            return;
        }

        world.execute(() -> {
            try {
                if (!playerRef.isValid()) {
                    cancelMove(playerId);
                    return;
                }

                Ref<EntityStore> entityRef = playerRef.getReference();
                if (entityRef == null || !entityRef.isValid()) {
                    cancelMove(playerId);
                    return;
                }

                Store<EntityStore> store = entityRef.getStore();
                TransformComponent transform = store.getComponent(entityRef, TransformComponent.getComponentType());
                PlayerInput input = store.getComponent(entityRef, PlayerInput.getComponentType());
                if (transform == null || input == null) {
                    cancelMove(playerId);
                    return;
                }

                Vector3d pos = transform.getPosition();
                double dx = targetPos.x - pos.x;
                double dz = targetPos.z - pos.z;
                double distSq = dx * dx + dz * dz;
                double stopDist = Math.max(0.1, stopDistance);

                if (distSq <= stopDist * stopDist) {
                    queueStop(input);
                    cancelMove(playerId);
                    return;
                }

                double dist = Math.sqrt(distSq);
                double step = moveSpeed * (MOVE_TICK_MS / 1000.0);
                double stepX = (dx / dist) * step;
                double stepZ = (dz / dist) * step;

                Direction facing = new Direction(0.0f, (float) Math.atan2(-dx, dz), 0.0f);
                input.queue(new PlayerInput.SetBody(facing));

                MovementStates states = new MovementStates();
                states.walking = true;
                states.idle = false;
                states.horizontalIdle = false;
                input.queue(new PlayerInput.SetMovementStates(states));

                input.queue(new PlayerInput.RelativeMovement(stepX, 0.0, stepZ));
            } catch (Exception e) {
                cancelMove(playerId);
                LOGGER.atSevere().withCause(e).log("[RTSCamera:Move] Error during click-to-move tick!");
            }
        });
    }

    private void queueStop(PlayerInput input) {
        MovementStates states = new MovementStates();
        states.idle = true;
        states.horizontalIdle = true;
        input.queue(new PlayerInput.SetMovementStates(states));
        input.queue(new PlayerInput.SetClientVelocity(new com.hypixel.hytale.protocol.Vector3d(0.0, 0.0, 0.0)));
    }
}
