package tconstruct.items.accessory;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import tconstruct.library.IAccessory;
import tconstruct.library.IModifyable;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.tools.ToolCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AccessoryCore extends Item implements IAccessory, IModifyable
{
    /**
     * Override getArmorModel() to have render on the player.
     */
    protected String texture;
    public AccessoryCore(int par1, String texture)
    {
        super(par1);
        this.setCreativeTab(TConstructRegistry.materialTab);
        this.setMaxStackSize(1);
        this.texture = texture;
    }

    @Override
    public String getBaseTag ()
    {
        return "TinkerAccessory";
    }

    @Override
    public String getModifyType ()
    {
        return "Accessory";
    }

    final static String[] traits = new String[] { "accessory" };

    @Override
    public String[] getTraits ()
    {
        return traits;
    }

    /*public void getSubItems (int id, CreativeTabs tab, List list)
    {
        ItemStack glove = new ItemStack(this);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Built", true);
        tag.setInteger("Modifiers", 5);
        baseTag.setTag(getBaseTag(), tag);
        glove.setTagCompound(baseTag);
        list.add(glove);
    }*/
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(getDefaultItem());
    }
    
    public ItemStack getDefaultItem()
    {
        ItemStack gear = new ItemStack(this.itemID, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();
        
        int baseDurability = 500;
        
        baseTag.setInteger("Damage", 0); //Damage is damage to the tool
        baseTag.setInteger("TotalDurability", baseDurability);
        baseTag.setInteger("BaseDurability", baseDurability);
        baseTag.setInteger("BonusDurability", 0); //Modifier
        baseTag.setFloat("ModDurability", 0f); //Modifier
        baseTag.setBoolean("Broken", false);
        baseTag.setBoolean("Built", true);

        gear.setTagCompound(baseTag);
        return gear;
    }

    /* Icons */

    @SideOnly(Side.CLIENT)
    protected Icon[] modifiers;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon("tinker:"+texture);
        registerModifiers(iconRegister);
    }

    @SideOnly(Side.CLIENT)
    protected void registerModifiers(IconRegister iconRegister)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        if (renderPass > 0)
        {
            if (stack.hasTagCompound())
            {
                NBTTagCompound tags = stack.getTagCompound().getCompoundTag("TinkerAccessory");
                if (renderPass == 1 && tags.hasKey("Effect1"))
                {
                    return modifiers[tags.getInteger("Effect1")];
                }
                if (renderPass == 2 && tags.hasKey("Effect2"))
                {
                    return modifiers[tags.getInteger("Effect2")];
                }
                if (renderPass == 3 && tags.hasKey("Effect3"))
                {
                    return modifiers[tags.getInteger("Effect3")];
                }
            }
            return ToolCore.blankSprite;
        }

        return itemIcon;
    }

    /* Tooltips */

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;

        NBTTagCompound tags = stack.getTagCompound();
        if (tags.hasKey("TinkerAccessory"))
        {
            boolean broken = tags.getCompoundTag("TinkerAccessory").getBoolean("Broken");
            if (broken)
                list.add("\u00A7oBroken");
            else
            {
                boolean displayToolTips = true;
                int tipNum = 0;
                while (displayToolTips)
                {
                    tipNum++;
                    String tooltip = "Tooltip" + tipNum;
                    if (tags.getCompoundTag("TinkerAccessory").hasKey(tooltip))
                    {
                        String tipName = tags.getCompoundTag("TinkerAccessory").getString(tooltip);
                        if (!tipName.equals(""))
                            list.add(tipName);
                    }
                    else
                        displayToolTips = false;
                }
            }
        }
    }
}
