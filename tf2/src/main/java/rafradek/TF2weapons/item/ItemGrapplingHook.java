package rafradek.TF2weapons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.client.audio.TF2Sounds;
import rafradek.TF2weapons.common.MapList;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.EntityDummy;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntityScout;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.projectile.EntityBall;
import rafradek.TF2weapons.entity.projectile.EntityGrapplingHook;
import rafradek.TF2weapons.entity.projectile.EntityProjectileBase;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class ItemGrapplingHook extends ItemProjectileWeapon {

	
	public void onProjectileShoot(ItemStack stack, EntityProjectileBase proj, EntityLivingBase living, World world, int thisCritical, EnumHand hand) {
		
		if (proj instanceof EntityGrapplingHook && WeaponsCapability.get(living) != null) {
			WeaponsCapability.get(living).setGrapplingHook((EntityGrapplingHook) proj);
		}
	}
	
	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		return super.canFire(world, living, stack) && WeaponsCapability.get(living) != null && !WeaponsCapability.get(living).isGrappling() && TF2Util.pierce(world, living, 256, false, 1f, false).get(0).typeOfHit != RayTraceResult.Type.MISS;
	}
	
	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		boolean use = super.endUse(stack, living, world, action, newState);
		
		if ((newState & 1) == 0 && WeaponsCapability.get(living) != null && WeaponsCapability.get(living).getGrapplingHook() != null && WeaponsCapability.get(living).getGrapplingHook().stickedEntity == null) {
			WeaponsCapability.get(living).setGrapplingHook(null);
		}
		
		return use;
	}
	
	@Override
	public boolean startUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		boolean use = super.endUse(stack, living, world, action, newState);
		
		if ((newState & 1) == 1 && (action & 1) == 0 && WeaponsCapability.get(living) != null && WeaponsCapability.get(living).getGrapplingHook() != null && WeaponsCapability.get(living).getGrapplingHook().stickedEntity != null) {
			WeaponsCapability.get(living).setGrapplingHook(null);
		}
		
		return use;
	}
}
