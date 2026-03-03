package net.lax1dude.eaglercraft.v1_8;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovingObjectPosition;
import java.util.Random;

public class Triggerbot {
    private static long lastAttack = 0;
    private static long nextDelay = 0;
    private static final Random rand = new Random();

    public static void onTick(Minecraft mc) {
        if (mc.currentScreen != null || mc.thePlayer.isSpectator()) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) mc.objectMouseOver.entityHit;

                if (target.isInvisible() || mc.thePlayer.isOnSameTeam(target)) return;

                long now = System.currentTimeMillis();
                if (now - lastAttack >= nextDelay) {
                    // MACE/CRIT LOGIC: Wait for falling
                    boolean isFalling = mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater();
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !isFalling) return;

                    mc.playerController.attackEntity(mc.thePlayer, target);
                    mc.thePlayer.swingItem();
                    
                    lastAttack = now;
                    nextDelay = 200 + rand.nextInt(300); 
                }
            }
        }
    }
}
