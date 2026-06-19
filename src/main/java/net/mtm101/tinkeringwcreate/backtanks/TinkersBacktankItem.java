package net.mtm101.tinkeringwcreate.backtanks;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;
import org.jetbrains.annotations.NotNull;
import org.openjdk.nashorn.internal.objects.annotations.Getter;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.helper.ArmorUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.armor.DummyArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem.PIGLIN_NEUTRAL;
import static slimeknights.tconstruct.library.tools.nbt.ToolStack.TAG_MATERIALS;

public class TinkersBacktankItem extends BacktankItem implements IModifiable, IModifiableDisplay {

    // copy of implementation in BlockEntity
    public @NotNull ModelData getBlockModelData(ItemStack stack) {
        return ModelData.builder().with(ModelProperties.MATERIALS, Objects.requireNonNullElse(MaterialIdNBT.readFromNBT(stack.getTag().get(TAG_MATERIALS)), MaterialIdNBT.EMPTY)).build();
    }

    //@Getter
    protected ToolDefinition toolDefinition;

    protected ResourceLocation name;

    /** Cached tool for rendering on UIs */
    private ItemStack toolForRendering;

    /*
    public TinkersBacktankItem(ArmorMaterial materialIn, Properties builderIn, ToolDefinition toolDefinition, Supplier<BacktankBlockItem> placeable) {
        super(materialIn, builderIn, Create.asResource("copper_diving"), placeable);
        this.toolDefinition = toolDefinition;
    }

    public TinkersBacktankItem(ModifiableArmorMaterial material, Properties properties, Supplier<BacktankBlockItem> placeable) {
        this(material, properties, Objects.requireNonNull(material.getArmorDefinition(TYPE), "Missing tool definition for " + TYPE.getName()), placeable);
    }*/

    @SuppressWarnings("removal")
    public TinkersBacktankItem(Properties builderIn, ToolDefinition toolDefinition, Supplier<BacktankBlockItem> placeable) {
        super(AllArmorMaterials.COPPER, builderIn, Create.asResource("copper_diving"), placeable);
        this.toolDefinition = toolDefinition;
        this.name = new ResourceLocation(TinkeringWCreate.MOD_ID, "tinkers_backtank");
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    // tinker visuals

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ArmorUtil.getDummyArmorTexture(slot);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new ArmorModelManager.ArmorModelDispatcher() {
            @Override
            protected ResourceLocation getName() {
                return name;
            }
        });
    }


    // tinker implement stuff

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return ModifierUtil.checkVolatileFlag(stack, PIGLIN_NEUTRAL);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.isCurse() && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentModifierHook.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public Map<Enchantment,Integer> getAllEnchantments(ItemStack stack) {
        return EnchantmentModifierHook.getAllEnchantments(stack);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ModifierUtil.canPerformAction(ToolStack.from(stack), toolAction);
    }

    @Override
    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag nbt) {
        ToolStack.verifyTag(this, nbt, getToolDefinition());
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level levelIn, Player playerIn) {
        ToolStack.ensureInitialized(stack, getToolDefinition());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        // handle in the tinker station
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.isCrouching()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            InteractionResult result = ToolInventoryCapability.tryOpenContainer(stack, null, getToolDefinition(), playerIn, Util.getSlotType(handIn));
            if (result.consumesAction()) {
                return new InteractionResultHolder<>(result, stack);
            }
        }
        return super.use(levelIn, playerIn, handIn);
    }


    /* Armor properties */

    @Override
    public Multimap<Attribute,AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
        if (slot != getEquipmentSlot()) {
            return ImmutableMultimap.of();
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        if (!tool.isBroken()) {
            // base stats
            StatsNBT statsNBT = tool.getStats();
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(type);
            float armor = statsNBT.get(ToolStats.ARMOR);
            if (armor > 0) {
                builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "tconstruct.armor.armor", armor, AttributeModifier.Operation.ADDITION));
            }
            float toughness = statsNBT.get(ToolStats.ARMOR_TOUGHNESS);
            if (toughness > 0) {
                builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "tconstruct.armor.toughness", toughness, AttributeModifier.Operation.ADDITION));
            }
            double knockbackResistance = statsNBT.get(ToolStats.KNOCKBACK_RESISTANCE);
            if (knockbackResistance > 0) {
                builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "tconstruct.armor.knockback_resistance", knockbackResistance, AttributeModifier.Operation.ADDITION));
            }
            // grab attributes from modifiers
            BiConsumer<Attribute,AttributeModifier> attributeConsumer = builder::put;
            for (ModifierEntry entry : tool.getModifierList()) {
                entry.getHook(ModifierHooks.ATTRIBUTES).addAttributes(tool, entry, slot, attributeConsumer);
            }
        }

        return builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (slot != getEquipmentSlot() || nbt == null) {
            return ImmutableMultimap.of();
        }
        return getAttributeModifiers(ToolStack.from(stack), slot);
    }



    /* Ticking */

    @Override
    public void inventoryTick(ItemStack stack, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // don't care about non-living, they skip most tool context
        if (entityIn instanceof LivingEntity living) {
            ToolStack tool = ToolStack.from(stack);
            if (!levelIn.isClientSide) {
                tool.ensureHasData();
            }
            List<ModifierEntry> modifiers = tool.getModifierList();
            if (!modifiers.isEmpty()) {
                boolean isCorrectSlot = living.getItemBySlot(getEquipmentSlot()) == stack;
                // we pass in the stack for most custom context, but for the sake of armor its easier to tell them that this is the correct slot for effects
                for (ModifierEntry entry : modifiers) {
                    entry.getHook(ModifierHooks.INVENTORY_TICK).onInventoryTick(tool, entry, levelIn, living, itemSlot, isSelected, isCorrectSlot, stack);
                }
            }
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
        return SlotStackModifierHook.overrideStackedOnOther(held, slot, action, player) || super.overrideStackedOnOther(held, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return SlotStackModifierHook.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access) || super.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access);
    }


    /* Tooltips */

    @Override
    public Component getName(ItemStack stack) {
        return ToolNameHook.getName(getToolDefinition(), stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
    }

    @Override
    public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
        tooltips = TooltipUtil.getArmorStats(tool, player, tooltips, key, tooltipFlag);
        TooltipUtil.addAttributes(this, tool, player, tooltips, TooltipUtil.SHOW_ARMOR_ATTRIBUTES, getEquipmentSlot());
        return tooltips;
    }

    @Override
    public int getDefaultTooltipHideFlags(ItemStack stack) {
        return TooltipUtil.getModifierHideFlags(getToolDefinition());
    }

    /* Display items */

    @Override
    public ItemStack getRenderTool() {
        if (toolForRendering == null) {
            toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }
        return toolForRendering;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // we use enchantments to handle some modifiers, so don't glow from them
        // however, if a modifier wants to glow let them
        return ModifierUtil.checkVolatileFlag(stack, SHINY);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return RarityModule.getRarity(stack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return IndestructibleItemEntity.hasCustomEntity(stack);
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity original, ItemStack stack) {
        return IndestructibleItemEntity.createFrom(level, original, stack);
    }
}
