package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import micdoodle8.mods.galacticraft.core.items.IShiftDescription;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCompressor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDecompressor;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class BlockOxygenCompressor extends BlockAdvancedTile implements IShiftDescription, ISortableBlock
{
    public static final int OXYGEN_COMPRESSOR_METADATA = 0;
    public static final int OXYGEN_DECOMPRESSOR_METADATA = 4;

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumCompressorType.class);

    public enum EnumCompressorType implements IStringSerializable
    {
        COMPRESSOR(0, "compressor"),
        DECOMPRESSOR(1, "decompressor");

        private final int meta;
        private final String name;

        EnumCompressorType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public static EnumCompressorType byMetadata(int meta)
        {
            return values()[meta];
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }

    public BlockOxygenCompressor(boolean isActive, String assetName)
    {
        super(Material.ROCK);
        this.setHardness(1.0F);
        this.setSoundType(SoundType.METAL);
        this.setUnlocalizedName(assetName);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @Override
    public boolean onUseWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int metadata = getMetaFromState(world.getBlockState(pos));
        int change = world.getBlockState(pos).getValue(FACING).rotateY().getHorizontalIndex();

        world.setBlockState(pos, this.getStateFromMeta(metadata - (metadata % 4) + change), 3);

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBaseUniversalElectrical)
        {
            ((TileBaseUniversalElectrical) te).updateFacing();
        }

        return true;
    }

    @Override
    public boolean onMachineActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        entityPlayer.openGui(GalacticraftCore.instance, -1, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        int metadata = getMetaFromState(state);
        if (metadata >= BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA)
        {
            return new TileEntityOxygenDecompressor();
        }
        else if (metadata >= BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA)
        {
            return new TileEntityOxygenCompressor();
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        final int angle = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int change = EnumFacing.getHorizontal(angle).getOpposite().getHorizontalIndex();

        if (stack.getItemDamage() >= BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA)
        {
            change += BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA;
        }
        else if (stack.getItemDamage() >= BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA)
        {
            change += BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA;
        }

        worldIn.setBlockState(pos, getStateFromMeta(change), 3);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        list.add(new ItemStack(this, 1, BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA));
        list.add(new ItemStack(this, 1, BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        int metadata = getMetaFromState(state);
        if (metadata >= BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA)
        {
            return BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA;
        }
        else if (metadata >= BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA)
        {
            return BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public String getShiftDescription(int meta)
    {
        switch (meta)
        {
        case OXYGEN_COMPRESSOR_METADATA:
            return GCCoreUtil.translate("tile.oxygen_compressor.description");
        case OXYGEN_DECOMPRESSOR_METADATA:
            return GCCoreUtil.translate("tile.oxygen_decompressor.description");
        }
        return "";
    }

    @Override
    public boolean showDescription(int meta)
    {
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta % 4);
        EnumCompressorType type = EnumCompressorType.byMetadata((int) Math.floor(meta / 4.0));
        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(TYPE, type);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing) state.getValue(FACING)).getHorizontalIndex() + ((EnumCompressorType) state.getValue(TYPE)).getMeta() * 4;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, TYPE);
    }

    @Override
    public EnumSortCategoryBlock getCategory(int meta)
    {
        return EnumSortCategoryBlock.MACHINE;
    }
}
