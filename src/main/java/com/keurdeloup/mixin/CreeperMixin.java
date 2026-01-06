package com.keurdeloup.mixin;

import com.keurdeloup.FelinePresence;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {

    protected CreeperMixin() {
        super(null, null);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    protected void addAvoidPlayerWithFelinePresence(CallbackInfo ci) {
        // Mimics the existing avoid cat behavior but for players with Feline Presence enchantment
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(
            (Creeper)(Object)this,
            LivingEntity.class,
            this::hasFelinePresenceEnchantment,  // Predicate to check for enchantment
            6.0F,      // Max distance to avoid (same as cat avoidance)
            1.0D,      // Walk speed modifier
            1.2D,      // Sprint speed modifier
            EntitySelector.NO_SPECTATORS::test  // Additional predicate
        ));
    }

    /**
     * Checks if the living entity has any armor piece with the Feline Presence enchantment
     */
    @Unique
    private boolean hasFelinePresenceEnchantment(LivingEntity entity) {
        return FelinePresence.hasFelinePresence(entity);
    }
}
