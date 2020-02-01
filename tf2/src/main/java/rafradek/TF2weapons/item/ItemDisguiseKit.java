package rafradek.TF2weapons.item;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.client.gui.GuiDisguiseKit;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntitySpy;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;

public class ItemDisguiseKit extends Item implements IItemSlotNumber, IItemOverlay {

	public ItemDisguiseKit() {
		this.setCreativeTab(TF2weapons.tabutilitytf2);
		this.setMaxStackSize(1);
		this.setMaxDamage(80);
	}

	public static void startDisguise(EntityLivingBase living, World world, String type) {
		String type2 = type;
		if (!world.isRemote && type.startsWith("T:") && (living instanceof EntityPlayer)) {
			EntityPlayer player = world.getClosestPlayer(living.posX, living.posY, living.posZ, 512, (playerl) -> {
				return !TF2Util.isOnSameTeam(living, playerl) && WeaponsCapability.get(playerl).getUsedToken() >=0 && type.substring(2).equalsIgnoreCase(ItemToken.CLASS_NAMES[WeaponsCapability.get(playerl).getUsedToken()]);
			});
			if (player != null) {
				type2 = "P:"+player.getName();
			}
		}
		WeaponsCapability.get(living).setDisguiseType(type2);
		if (living.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks == 0)
			// System.out.println("starting disguise");
			if (!world.isRemote)
			living.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks = 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		if (world.isRemote && !TF2ConfigVars.limitedDisguise && ItemToken.allowUse(living, "spy") && TF2Util.getFirstItem(living.inventory, stack -> TF2Attribute.getModifier("No Disguise Kit", stack, 0, living) != 0).isEmpty())
			ClientProxy.showGuiDisguise();
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}

	public static boolean isDisguised(EntityLivingBase living, EntityLivingBase view) {
		if(!living.hasCapability(TF2weapons.WEAPONS_CAP, null) || !WeaponsCapability.get(living).isDisguised() 
				|| (living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks != 0 && !(view instanceof EntityBuilding)))
			return false;
		String disguisetype=WeaponsCapability.get(living).getDisguiseType();
		if(disguisetype.startsWith("M:") || disguisetype.startsWith("T:"))
			return true;
		if(disguisetype.startsWith("P:")) {
			return living.world.getScoreboard().getPlayersTeam(disguisetype.substring(2)) == view.getTeam();
		}
		return false;
	}
	
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return repair.getItem() == TF2weapons.itemTF2 && repair.getMetadata() == 2;
    }

	@Override
	public boolean showInfoBox(ItemStack stack, EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void drawOverlay(ItemStack stack, EntityPlayer player, Tessellator tessellator, BufferBuilder buffer,
			ScaledResolution resolution) {
		if (ItemToken.allowUse(player, "spy") && TF2ConfigVars.limitedDisguise && TF2Util.getFirstItem(player.inventory, stackl -> TF2Attribute.getModifier("No Disguise Kit", stackl, 0, player) != 0).isEmpty()) {
		GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		for(int i = 0; i < 9; i++) {
			gui.drawCenteredString(gui.getFontRenderer(), ItemToken.CLASS_NAMES[i], width/2-225+i*50, height/2+50, 0xFFFFFFFF);
			gui.drawCenteredString(gui.getFontRenderer(), String.valueOf(i+1), width/2-225+i*50, height/2+60, 0xFFFFFFFF);
			EntityTF2Character entity = EntityTF2Character.createByClassId(player.world, i);
			entity.setEntTeam(TF2Util.getTeamForDisplay(player) == 0 ? 1 : 0);
			entity.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks=0;
			GuiDisguiseKit.drawEntityOnScreen(width/2-225+i*50, height/2+40, 35, entity);
		}
		}
	}

	@Override
	public boolean catchSlotHotkey(ItemStack stack, EntityPlayer player) {
		// TODO Auto-generated method stub
		return TF2ConfigVars.limitedDisguise;
	}

	@Override
	public void onSlotSelection(ItemStack stack, EntityPlayer player, int slot) {
		if (ItemToken.allowUse(player, "spy") && TF2Util.getFirstItem(player.inventory, stackl -> TF2Attribute.getModifier("No Disguise Kit", stackl, 0, player) != 0).isEmpty()) {
			TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("T:" + ItemToken.CLASS_NAMES[slot]));
		}
	}
}
