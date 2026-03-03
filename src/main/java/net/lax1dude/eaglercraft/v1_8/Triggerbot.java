package net.lax1dude.eaglercraft.v1_8;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ChatComponentText;
import java.util.Random;

public class Triggerbot {
    private static long lastAttack = 0;
    private static long nextDelay = 0;
    private static final Random rand = new Random();

    public static void onTick(Minecraft mc) {
        // LOBBY SAFETY: Won't run in menus, spectator, or adventure mode
        if (mc.currentScreen != null || mc.thePlayer.isSpectator() || mc.playerController.getCurrentGameType().isAdventure()) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) mc.objectMouseOver.entityHit;

                // REACH CHECK: 3.5 Blocks
                if (mc.thePlayer.getDistanceToEntity(target) > 3.5F) return;

                // FILTERS: No teammates, no invisibles
                if (target.isInvisible() || mc.thePlayer.isOnSameTeam(target)) return;
                
                long now = System.currentTimeMillis();
                if (now - lastAttack >= nextDelay) {
                    
                    // CRIT LOGIC: If jumping, wait for the fall
                    boolean inAir = !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater();
                    boolean isFalling = mc.thePlayer.fallDistance > 0.0F;
                    if (inAir && !isFalling) return;

                    // THE CHAT TEST: Shows you the bot is firing
                    mc.thePlayer.addChatMessage(new ChatComponentText("§a[REDUX] Triggerbot Attack!"));

                    // THE ATTACK
                    mc.playerController.attackEntity(mc.thePlayer, target);
                    mc.thePlayer.swingItem();
                    
                    lastAttack = now;
                    // RECHARGE DELAY: 0.6s - 0.8s
                    nextDelay = 600 + rand.nextInt(201); 
                }
            }
        }
    }
}
