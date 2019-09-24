package rafradek.TF2weapons.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemJetpackTrigger extends ItemUsable implements IBackpackItem {

	public ItemJetpackTrigger() {
		super();
		this.setCreativeTab(TF2weapons.tabutilitytf2);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		if (!(ItemBackpack.getBackpack(Minecraft.getMinecraft().player).getItem() instanceof ItemJetpack))
			return false;
		return ItemBackpack.getBackpack(Minecraft.getMinecraft().player).getTagCompound().getShort("Charge") > 0;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		ItemStack backpack = ItemBackpack.getBackpack(Minecraft.getMinecraft().player);
		if (!(backpack.getItem() instanceof ItemJetpack))
			return 0;
		return (double)( backpack.getTagCompound().getShort("Charge"))/((ItemJetpack) backpack.getItem()).getCooldown(stack, null);
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		this.checkItem(par1ItemStack, par2World, par3Entity, par4, par5);
	}
	
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand, PredictionMessage message) {
		ItemStack jetpack = ItemBackpack.getBackpack(living);
		if(!world.isRemote && jetpack.getItem() instanceof ItemJetpack && ((ItemJetpack)jetpack.getItem()).canActivate(jetpack, living)) {
			((ItemJetpack)jetpack.getItem()).activateJetpack(jetpack, living, false);
		}
		return false;
	}
	
	@Override
	public void altUse(ItemStack stack, EntityLivingBase living, World world) {
		this.use(stack, living, world, EnumHand.MAIN_HAND, null);
	}
	
	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return (short) this.getFiringSpeed(item, player);
	}
	
	@Override
	public boolean showInfoBox(ItemStack stack, EntityPlayer player) {
		return true;
	}

	@Override
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		ItemStack backpack = ItemBackpack.getBackpack(player);
		if (backpack.getItem() instanceof ItemJetpack) {
			String charge = "";
			int progress = 20 - (int)((float)backpack.getTagCompound().getShort("Charge")/(float)((ItemJetpack) backpack.getItem()).getCooldown(backpack, player)*20f);
			for(int i=0;i<20;i++){
				if(i<progress)
					charge=charge+"|";
				else
					charge=charge+".";
			}
			return new String[]{"CHARGES: "+backpack.getTagCompound().getByte("Charges"),charge};
		}
		return new String[]{"CHARGES: 0",""};
	}
}
