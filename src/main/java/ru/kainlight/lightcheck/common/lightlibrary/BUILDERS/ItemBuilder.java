package ru.kainlight.lightcheck.common.lightlibrary.BUILDERS;

import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.common.lightlibrary.UTILS.Parser;

import javax.annotation.Nonnegative;
import java.util.*;

@SuppressWarnings("all")
@NoArgsConstructor
public final class ItemBuilder {
    private final HashMap<String, Object> data = new HashMap<>();
    private ItemStack itemStack;
    private Component displayName;
    private Material material;
    private int amount;
    private ItemMeta itemMeta;
    private boolean glow;
    private Color color;
    private int damage;
    private String base64;
    private String skullName;
    private UUID skullOwner;
    private List<ItemFlag> flags = new ArrayList<>();
    private List<Component> lore = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta.hasLore()) {
            this.lore = this.itemMeta.lore();
        }
        if (this.itemMeta.hasEnchants()) {
            this.enchantments = this.itemMeta.getEnchants();
        }
        if (this.itemMeta.hasDisplayName()) {
            this.displayName = this.itemMeta.displayName();
        }
        if (!this.itemMeta.getItemFlags().isEmpty()) {
            this.flags = Arrays.asList(this.itemMeta.getItemFlags().toArray(new ItemFlag[0]));
        }
    }

    public ItemBuilder(@NotNull Material material, @Nonnegative int amount) {
        if (amount > this.material.getMaxStackSize() || amount > 64) {
            amount = this.material.getMaxStackSize();
        }

        this.material = material;
        this.amount = amount <= 0 ? 1 : amount;
    }

    public ItemBuilder(@NotNull Material material) {
        this.material = material;
        this.amount = 1;
    }

    public ItemBuilder amount(@Nonnegative int amount) {
        if (amount > this.material.getMaxStackSize() || amount > 64) {
            amount = this.material.getMaxStackSize();
        }

        this.amount = amount <= 0 ? 1 : amount;
        return this;
    }

    public ItemBuilder skullFromUuid(@NotNull UUID uuid) {
        this.skullOwner = uuid;
        return this;
    }

    public ItemBuilder skullFromBase64(@NotNull String base64) {
        this.base64 = base64;
        return this;
    }

    public ItemBuilder skullFromName(@NotNull String name) {
        this.skullName = name;
        return this;
    }

    public ItemBuilder color(@NotNull Color color) {
        if (!this.material.name().toLowerCase(Locale.ROOT).split("_")[0].equalsIgnoreCase("leather")) {
            throw new IllegalStateException("The material must be a leather equipment part.");
        }
        this.color = color;
        return this;
    }

    public ItemBuilder glow() {
        this.glow = true;
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        if (displayName == null) {
            this.displayName = Component.text("");
            return this;
        }

        this.displayName = Parser.get().hex(displayName);
        return this;
    }

    public ItemBuilder data(@NotNull String key, @NotNull Object value) {
        this.data.put(key, value);
        return this;
    }

    public ItemBuilder flags(@NotNull List<ItemFlag> flags) {
        this.flags = flags;
        return this;
    }

    public ItemBuilder flags(@NotNull ItemFlag... flags) {
        this.flags = Arrays.asList(flags);
        return this;
    }

    public ItemBuilder flag(@NotNull ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public ItemBuilder defaultFlags() {
        this.flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder clearLore() {
        if (this.itemMeta.getLore() == null || this.itemMeta.getLore().isEmpty()) return this;
        this.itemMeta.getLore().clear();
        return this;
    }

    public ItemBuilder loreString(String... lines) {
        List<Component> finalList = new ArrayList<>();

        for (String line : lines) {
            finalList.add(Component.text(line));
        }

        this.lore = finalList;
        return this;
    }

    public ItemBuilder loreString(List<String> lines) {
        List<Component> finalList = new ArrayList<>();

        for (String line : lines) {
            finalList.add(Component.text(line));
        }

        this.lore = finalList;
        return this;
    }

    public ItemBuilder lore(Component... lines) {
        List<Component> finalList = new ArrayList<>();

        for (Component line : lines) {
            finalList.add(line);
        }

        this.lore = finalList;
        return this;
    }

    public ItemBuilder lore(List<Component> lines) {
        List<Component> finalList = new ArrayList<>();

        for (Component line : lines) {
            finalList.add(line);
        }

        this.lore = finalList;
        return this;
    }

    public ItemBuilder appendLine(Component append) {
        this.lore.add(append);
        return this;
    }

    public ItemBuilder clearEnchants() {
        if (!this.itemMeta.hasEnchants()) return this;

        this.itemMeta.getEnchants().keySet().forEach(enchantment -> this.itemMeta.removeEnchant(enchantment));
        return this;
    }

    public ItemBuilder damage(@Nonnegative int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be at least 1.");
        }

        this.damage = damage;
        return this;
    }

    public ItemBuilder removeEnchant(@NotNull Enchantment enchantment) {
        this.itemMeta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder removeEnchant(@NotNull Enchantment... enchantments) {
        for (Enchantment enchantment : enchantments) {
            this.itemMeta.removeEnchant(enchantment);
        }
        return this;
    }

    public ItemBuilder enchant(@NotNull Enchantment enchantment) {
        this.enchantments.put(enchantment, 1);
        return this;
    }

    public ItemBuilder enchant(@NotNull Enchantment enchantment, @Nonnegative int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("Level must be at least 1.");
        }
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder enchant(@NotNull Enchantment... enchantments) {
        for (Enchantment enchantment : enchantments) {
            this.enchantments.put(enchantment, 1);
        }
        return this;
    }

    public ItemBuilder enchant(List<Enchantment> enchantments, List<Integer> level) {
        if (enchantments.size() != level.size()) {
            throw new IndexOutOfBoundsException("The passed parameters must have the same size.");
        }

        for (int i = 0; i < enchantments.size(); i++) {
            Enchantment enchantment = enchantments.get(i);
            int enchantmentLevel = level.get(i);

            this.enchantments.put(enchantment, enchantmentLevel);
        }
        return this;
    }

    public ItemBuilder enchant(List<Enchantment> enchantments, @Nonnegative int level) {
        for (Enchantment enchantment : enchantments) {
            this.enchantments.put(enchantment, level);
        }
        return this;
    }

    public ItemBuilder enchant(List<Enchantment> enchantments) {
        for (Enchantment enchantment : enchantments) {
            this.enchantments.put(enchantment, 1);
        }
        return this;
    }

    public ItemStack build() {
        if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.material, this.amount);
        }
        if (this.itemMeta == null) {
            this.itemMeta = this.itemStack.getItemMeta();
        }
        if (this.displayName != null) {
            this.itemMeta.displayName(this.displayName);
        }
        if (!this.lore.isEmpty()) {
            this.itemMeta.lore(this.lore);
        }
        if (!this.flags.isEmpty()) {
            this.flags.forEach(itemFlag -> this.itemMeta.addItemFlags(itemFlag));
        }
        if (!this.enchantments.isEmpty()) {
            this.enchantments.forEach((enchantment, level) -> this.itemMeta.addEnchant(enchantment, level, true));
        }

        this.itemStack.setAmount(this.amount);

        if (this.glow) {
            this.itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);

            if (!this.itemMeta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        this.itemStack.setItemMeta(this.itemMeta);

        return this.itemStack;
    }

}