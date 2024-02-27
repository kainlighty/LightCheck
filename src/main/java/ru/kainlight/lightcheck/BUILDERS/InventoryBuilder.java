package ru.kainlight.lightcheck.BUILDERS;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Consumer;

public final class InventoryBuilder implements Listener, Cloneable {
    private Inventory inventory;
    private Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();
    private List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();

    public InventoryBuilder(Plugin plugin, int size, boolean event) {
        inventory = Bukkit.createInventory(null, size);
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, Inventory inventory, boolean event) {
        this.inventory = inventory;
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, Player player, int size, String title, boolean event) {
        inventory = Bukkit.createInventory(player, size, title);
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, InventoryHolder holder, int size, boolean event) {
        inventory = Bukkit.createInventory(holder, size);
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, InventoryHolder holder, InventoryType type) {
        inventory = Bukkit.createInventory(holder, type);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, InventoryHolder holder, InventoryType type, Component title) {
        inventory = Bukkit.createInventory(holder, type, title);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, InventoryHolder holder, int size, String title, boolean event) {
        inventory = Bukkit.createInventory(holder, size, title);
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, InventoryHolder holder, int size, Component title, boolean event) {
        inventory = Bukkit.createInventory(holder, size, title);
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder(Plugin plugin, Component title, int size, boolean event) {
        inventory = Bukkit.createInventory(null, size, title);
        if(event) Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryBuilder setItems(LinkedList<Integer> index, LinkedList<ItemStack> items) {
        Iterator<Integer> slotIterator = index.iterator();
        Iterator<ItemStack> itemIterator = items.iterator();

        while (slotIterator.hasNext() && itemIterator.hasNext()) {
            int slot = slotIterator.next();
            ItemStack itemStack = itemIterator.next();
            inventory.setItem(slot, itemStack);
        }

        return this;
    }

    public InventoryBuilder setItem(int index, ItemStack itemStack) {
        inventory.setItem(index, itemStack);
        return this;
    }

    @SafeVarargs
    public final InventoryBuilder setItem(int index, ItemStack itemStack, Consumer<InventoryClickEvent>... consumers) {
        inventory.setItem(index, itemStack);
        List.of(consumers).forEach(inventoryClickEventConsumer -> itemHandlers.put(index, inventoryClickEventConsumer));
        return this;
    }

    public InventoryBuilder removeItem(int index) {
        inventory.setItem(index, null);
        itemHandlers.remove(index);
        return this;
    }

    public InventoryBuilder addItems(ItemStack... itemStacks) {
        inventory.addItem(itemStacks);
        return this;
    }

    public InventoryBuilder dragEvent(Consumer<InventoryDragEvent> event) {
        dragHandlers.add(event);
        return this;
    }

    public InventoryBuilder clickEvent(Consumer<InventoryClickEvent> event) {
        clickHandlers.add(event);
        return this;
    }

    public InventoryBuilder clickEvent(Consumer<InventoryClickEvent> event, Integer... slots) {
        Iterator<Integer> iterator = Arrays.stream(slots).iterator();
        while (iterator.hasNext()) {
            itemHandlers.put(iterator.next(), event);
        }
        return this;
    }

    public InventoryBuilder clickEvent(int slot, Consumer<InventoryClickEvent> event) {
        itemHandlers.put(slot, event);
        return this;
    }

    public InventoryBuilder openEvent(Consumer<InventoryOpenEvent> event) {
        openHandlers.add(event);
        return this;
    }

    public InventoryBuilder closeEvent(Consumer<InventoryCloseEvent> event) {
        closeHandlers.add(event);
        return this;
    }

    public InventoryBuilder open(Player player) {
        player.openInventory(inventory);
        return this;
    }

    public InventoryBuilder fillBorder(Material material, boolean rows) {
        int size = rows ? inventory.getSize() / 9 : inventory.getSize();
        ItemStack glassPane = new ItemBuilder(material).defaultFlags().displayName(" ").build();

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane); // Верхний ряд
            inventory.setItem((size - 1) * 9 + i, glassPane); // Нижний ряд
        }

        for (int i = 1; i < size - 1; i++) {
            inventory.setItem(i * 9, glassPane); // Левый столбец
            inventory.setItem(i * 9 + 8, glassPane); // Правый столбец
        }

        return this;
    }

    public Inventory build() {
        return inventory;
    }

    @EventHandler
    private void handleClick(InventoryClickEvent event) {
        if (!Objects.equals(event.getClickedInventory(), inventory)) return;

        clickHandlers.forEach(c -> c.accept(event));

        Consumer<InventoryClickEvent> clickConsumer = itemHandlers.get(event.getRawSlot());
        if (clickConsumer != null) clickConsumer.accept(event);
    }

    @EventHandler
    private void handleDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        dragHandlers.forEach(c -> c.accept(event));
    }

    @EventHandler
    private void handleOpen(InventoryOpenEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        openHandlers.forEach(c -> c.accept(event));
    }

    @EventHandler
    private void handleClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        closeHandlers.forEach(c -> c.accept(event));
    }

    @Override
    public InventoryBuilder clone() {
        try {
            InventoryBuilder clone = (InventoryBuilder) super.clone();
            clone.clickHandlers = this.clickHandlers;
            clone.dragHandlers = this.dragHandlers;
            clone.inventory = this.inventory;
            clone.itemHandlers = this.itemHandlers;
            clone.openHandlers = this.openHandlers;
            clone.closeHandlers = this.closeHandlers;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone operation is not correctly executed!");
        }
    }
}
