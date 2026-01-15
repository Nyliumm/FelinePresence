package com.keurdeloup.mixin;

import com.keurdeloup.FelinePresence;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {

    @Shadow public abstract void setSwellDir(int swellDir);
    protected CreeperMixin() {
        super(null, null);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    protected void addAvoidPlayerWithFelinePresence(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(
            (Creeper)(Object)this,
            LivingEntity.class,
            this::hasFelinePresenceEnchantment,
            12.0F,      // Max distance to avoid (6 = same as cats)
            1.2D,       // Walk speed modifier (1.0 default)
            1.4D,       // Sprint speed modifier (1.2 default)
            EntitySelector.NO_SPECTATORS::test
        ));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void felinePresence$stopExplosion(CallbackInfo ci) {
        LivingEntity target = this.getTarget();
        if (target != null && this.hasFelinePresenceEnchantment(target)) {
            this.setSwellDir(-1);
        }
    }

    @Unique
    private boolean hasFelinePresenceEnchantment(LivingEntity entity) {
        return FelinePresence.hasFelinePresence(entity);
    }
}
