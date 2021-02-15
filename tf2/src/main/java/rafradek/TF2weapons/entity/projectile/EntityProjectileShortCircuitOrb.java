package rafradek.TF2weapons.entity.projectile;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class EntityProjectileShortCircuitOrb extends EntityProjectileSimple {

	double struck;
	
	public EntityProjectileShortCircuitOrb(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.setType(5);
	}
	
	
	public boolean isPushable() {
		return false;
	}
	
	public boolean canPenetrate() {
		return true;
	}
	
	@Override
	public double getGravity() {
		return 0;
	}
	
	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		super.onHitMob(entityHit, mop);
		if (entityHit instanceof EntityProjectileBase && ((EntityProjectileBase)entityHit).isPushable()) {
			entityHit.setDead();
			this.playSound(ItemFromData.getSound(usedWeapon, PropertyType.SPECIAL_1_SOUND), 1.0f, 1.0f);
		}
	}
	
	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (this.struck == 0)
			super.onHitGround(x, y, z, mop);
	}
	
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.hitEntities.size() > 0) {
			this.hitEntities.clear();
		}
	}
}
