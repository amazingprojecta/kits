
package dev.jpcode.kits;

import net.minecraft.util.Prediction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class InventoryUtil {

    private InventoryUtil() {}

    public static void offerAllCopies(KitInventory source, Inventory target) {
        for (int i = 0; i < KitInventory.MAIN_SIZE; ++i) {
            ItemStack stack = source.main.get(i);

            if (!stack.isEmpty()) {
                target.placeItemBackInInventory(
                    stack.copy(),
                    Prediction.SERVER_ONLY
                );
            }
        }
    }

    public static void equipKit(KitInventory source, ServerPlayer player) {
        Inventory target = player.getInventory();

        EquipmentSlot[] armorSlots = {
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
        };

        // Equip only armor actually saved in the kit
        for (int i = 0; i < armorSlots.length; i++) {
            ItemStack kitStack = source.armor.get(i);

            if (kitStack.isEmpty()) continue;

            EquipmentSlot slot = armorSlots[i];
            ItemStack previous = player.getItemBySlot(slot);

            if (!previous.isEmpty()) {
                target.placeItemBackInInventory(
                    previous.copy(),
                    Prediction.SERVER_ONLY
                );
            }

            player.setItemSlot(slot, kitStack.copy());
        }

        // Replace offhand only when the kit has an offhand item
        ItemStack kitOffhand = source.offHand.get(0);

        if (!kitOffhand.isEmpty()) {
            ItemStack previous =
                player.getItemBySlot(EquipmentSlot.OFFHAND);

            if (!previous.isEmpty()) {
                target.placeItemBackInInventory(
                    previous.copy(),
                    Prediction.SERVER_ONLY
                );
            }

            player.setItemSlot(
                EquipmentSlot.OFFHAND,
                kitOffhand.copy()
            );
        }

        // Give the remaining kit items
        offerAllCopies(source, target);

        player.containerMenu.broadcastChanges();
    }
}
