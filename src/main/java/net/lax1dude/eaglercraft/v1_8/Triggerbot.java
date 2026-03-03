package net.lax1dude.eaglercraft.v1_8;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import java.util.Random;

public class Triggerbot {
    private static long lastAttack = 0;
    private static long nextDelay = 0;
    private static final Random rand = new Random();

    public static void onTick(Minecraft mc) {
        if (mc.currentScreen != null || mc.thePlayer.isSpectator() || mc.playerController.getCurrentGameType().isAdventure()) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) mc.objectMouseOver.entityHit;

                // REACH CHECK: Only swing if within 3.5 blocks
                if (mc.thePlayer.getDistanceToEntity(target) > 3.5F) return;

                if (target.isInvisible() || mc.thePlayer.isOnSameTeam(target)) return;
                if (mc.getNetHandler().getPlayerInfo(target.getUniqueID()) == null) return;

                long now = System.currentTimeMillis();
                if (now - lastAttack >= nextDelay) {
                    
                    // CRIT CHECK: If jumping, wait for the fall
                    boolean inAir = !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater();
                    boolean isFalling = mc.thePlayer.fallDistance > 0.0F;
                    if (inAir && !isFalling) return;

                    mc.playerController.attackEntity(mc.thePlayer, target);
                    mc.thePlayer.swingItem();
                    
                    lastAttack = now;
                    nextDelay = 600 + rand.nextInt(201); // 0.6s - 0.8s
                }
            }
        }
    }
}
