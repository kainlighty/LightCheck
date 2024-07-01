package ru.kainlight.lightcheck.common;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class Others {

    public static Others get() {
        return new Others();
    }

    public @NotNull Location getGroundLocation(@NotNull Location location) {
        Location clonedLocation = location.clone();
        Block block;

        // Используем do-while цикл для того, чтобы добавить (0, -1, 0) к локации игрока до тех пор, пока блок под ним не станет твердым
        do {
            clonedLocation.subtract(0, 1, 0);
            block = clonedLocation.getBlock();
        } while (!block.getType().isSolid() && clonedLocation.getY() > 0);

        // Изменяем Y-координату локации игрока на 0.3, чтобы предотвратить застревание в блоках
        clonedLocation.add(0, 0.3, 0);

        return clonedLocation;
    }


}
