package rafradek.TF2weapons.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.tileentity.TileEntityRobotDeploy;
import rafradek.TF2weapons.tileentity.TileEntityUpgrades;

public class BlockRobotDeploy extends BlockContainer {

	public static final PropertyBool HOLDER = PropertyBool.create("holder");
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyBool JOINED = PropertyBool.create("joined");
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public BlockRobotDeploy() {
		super(Material.IRON);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HOLDER, true).withProperty(JOINED, false).withProperty(ACTIVE, false));
		// TODO Auto-generated constructor stub
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if ((meta & 4) == 4) {
			TileEntityRobotDeploy upgrades = new TileEntityRobotDeploy();
			// upgrades.generateUpgrades();
			return upgrades;
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (state.getValue(HOLDER))
				FMLNetworkHandler.openGui(playerIn, TF2weapons.instance, 6, worldIn, pos.getX(), pos.getY(),
						pos.getZ());
			for (int x = -1; x < 2; x++)
				for (int y = -1; y < 1; y++)
					for (int z = -1; z < 2; z++)
						if (worldIn.getBlockState(pos.add(x, y, z)).getBlock() instanceof BlockRobotDeploy
								&& worldIn.getBlockState(pos.add(x, y, z)).getValue(HOLDER)) {
							FMLNetworkHandler.openGui(playerIn, TF2weapons.instance, 6, worldIn, pos.getX() + x,
									pos.getY() + y, pos.getZ() + z);
							return true;
						}
			
		}
		return true;
	}
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		
		
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		worldIn.setBlockState(pos, state = state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		
		if (placer instanceof EntityPlayer) {
			TileEntity ent = worldIn.getTileEntity(pos);
			for (EnumFacing facing: EnumFacing.HORIZONTALS) {
				BlockPos offpos = pos.offset(facing);
				TileEntity entoff = worldIn.getTileEntity(offpos);
				if (entoff instanceof TileEntityRobotDeploy && !worldIn.getBlockState(offpos).getValue(JOINED) && worldIn.getBlockState(offpos).getValue(HOLDER)) {
					EnumFacing placefacing = facing.rotateY();
					if (placefacing == placer.getHorizontalFacing()) {
						NBTTagCompound tag=new NBTTagCompound();
						entoff.writeToNBT(tag);
						tag.setInteger("x", pos.getX());
						tag.setInteger("y", pos.getY());
						tag.setInteger("z", pos.getZ());
						((TileEntityRobotDeploy)ent).readFromNBT(tag);
						worldIn.setBlockState(offpos, worldIn.getBlockState(offpos).withProperty(HOLDER, false));
						worldIn.setTileEntity(offpos, null);
						placefacing = placefacing.getOpposite();
					}
					else {
						state = state.withProperty(HOLDER, false);
					}
					state = state.withProperty(FACING, placefacing).withProperty(JOINED, true);
					worldIn.setBlockState(pos, state);
					worldIn.setBlockState(offpos, worldIn.getBlockState(offpos).withProperty(FACING, placefacing).withProperty(JOINED, true));
					if (worldIn.getBlockState(offpos.up()).getBlock() == this)
						worldIn.setBlockState(offpos.up(), worldIn.getBlockState(offpos.up()).withProperty(FACING, placefacing).withProperty(JOINED, true));
					break;
				}
			}
			
			/*TileEntity entbelow = worldIn.getTileEntity(pos.down());
			TileEntity entup = worldIn.getTileEntity(pos.up());
			IBlockState statebelow = worldIn.getBlockState(pos.down());
			IBlockState stateup = worldIn.getBlockState(pos.up());
			if (entbelow instanceof TileEntityRobotDeploy && !worldIn.getBlockState(pos.down()).getValue(JOINED)) {
				worldIn.setBlockState(pos, state = state.withProperty(JOINED, true));
				worldIn.setBlockState(pos, state = state.withProperty(HOLDER, false));
				worldIn.setTileEntity(pos, null);
				worldIn.setBlockState(pos.down(), statebelow = statebelow.withProperty(JOINED, true));
			}
			if (entup instanceof TileEntityRobotDeploy && !worldIn.getBlockState(pos.up()).getValue(JOINED)) {
				worldIn.setBlockState(pos, state.withProperty(JOINED, true));
				NBTTagCompound tag=new NBTTagCompound();
				((TileEntityRobotDeploy)ent).readFromNBT(entup.writeToNBT(tag));
				worldIn.setBlockState(pos.up(), stateup = stateup.withProperty(HOLDER, false));
				worldIn.setTileEntity(pos.up(), null);
				worldIn.setBlockState(pos.up(), stateup = stateup.withProperty(JOINED, true));
			}*/
			if (ent instanceof TileEntityRobotDeploy)
				((TileEntityRobotDeploy)ent).setOwner(placer.getName(),placer.getUniqueID());
		}
		if (worldIn.isAirBlock(pos.up()))
			worldIn.setBlockState(pos.up(), state.withProperty(HOLDER, false), 2);
	}
	
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		
		if (state.getValue(HOLDER)) {
			if (worldIn.getBlockState(pos.up()).getBlock() == this) {
				worldIn.setBlockToAir(pos.up());
			}
		}
		else {
			if (worldIn.getBlockState(pos.down()).getBlock() == this) {
				worldIn.setBlockToAir(pos.down());
			}
			if (worldIn.getBlockState(pos.up()).getBlock() == this && !worldIn.getBlockState(pos.up()).getValue(HOLDER)) {
				worldIn.setBlockToAir(pos.up());
			}
		}
		
		if (state.getValue(JOINED)) {
			if (state.getValue(HOLDER) || (worldIn.getBlockState(pos.down()).getBlock() == this && worldIn.getBlockState(pos.down()).getValue(HOLDER)))
				worldIn.destroyBlock(pos.offset(state.getValue(FACING).rotateY()), true);
			else
				worldIn.destroyBlock(pos.offset(state.getValue(FACING).rotateYCCW()), true);
		}
		
		TileEntity ent = worldIn.getTileEntity(pos);
		if (ent instanceof TileEntityRobotDeploy) {
			((TileEntityRobotDeploy) ent).dropInventory();
		}
    }
	
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up());
    }
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront((meta & 3) + 2)).withProperty(HOLDER,
				(meta & 4) == 4).withProperty(JOINED,(meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() - 2 + (state.getValue(HOLDER) ? 4 : 0) + (state.getValue(JOINED) ? 8 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, HOLDER, JOINED, ACTIVE });
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 4));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}
