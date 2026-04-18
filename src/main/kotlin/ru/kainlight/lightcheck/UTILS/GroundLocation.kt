package ru.kainlight.lightcheck.UTILS

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

internal class GroundLocation(val location: Location) {

    fun getGroundLocation(): Location {
        val clonedLocation: Location = location.clone()
        var block: Block

        do {
            clonedLocation.subtract(0.0, 1.0, 0.0)
            block = clonedLocation.block
        } while (!block.isSafeGround() && clonedLocation.y > 0)

        // Устанавливаем точную координату Y на верхнюю грань найденного блока
        clonedLocation.y = block.y + 1.0

        return clonedLocation
    }

    fun isSafeForTeleport(): Boolean {
        val block = location.block
        val aboveBlock = location.clone().add(0.0, 1.0, 0.0).block
        return block.type.isAir && aboveBlock.type.isAir
    }

    private fun Block.isSafeGround(): Boolean {
        return this.type.isSolid && this.type != Material.LADDER
    }
}