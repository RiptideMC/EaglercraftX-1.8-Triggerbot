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
        // SAFETY: Don't hit in menus, spectator, or adventure (Lobby Mode)
        if (mc.currentScreen != null || mc.thePlayer.isSpectator() || mc.playerController.getCurrentGameType().isAdventure()) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) mc.objectMouseOver.entityHit;

                // FILTERS: Teammates, Invisibles, and Anti-Cheat NPCs
                if (target.isInvisible() || mc.thePlayer.isOnSameTeam(target)) return;
                if (mc.getNetHandler().getPlayerInfo(target.getUniqueID()) == null) return;

                long now = System.currentTimeMillis();
                if (now - lastAttack >= nextDelay) {
                    
                    // CRITICAL HIT LOGIC: Only hits while falling (Mace Mode)
                    boolean isFalling = mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater();
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !isFalling) return;

                    // Execute Full-Strength Attack
                    mc.playerController.attackEntity(mc.thePlayer, target);
                    mc.thePlayer.swingItem();
                    
                    lastAttack = now;
                    // NEW DELAY: 600ms base + random up to 200ms (Total: 0.6s - 0.8s)
                    nextDelay = 600 + rand.nextInt(201); 
                }
            }
        }
    }
}
