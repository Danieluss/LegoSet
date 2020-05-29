package me.danieluss.ubiquitous_systems2

import android.graphics.Color
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.danieluss.ubiquitous_systems2.data.dto.ItemDetails
import kotlin.math.floor

object Utils {

    suspend fun <A, B> Iterable<A>.asyncMap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.awaitAll()
    }

    private fun interpolate(
        a: Float,
        b: Float,
        proportion: Float
    ): Float {
        return a + (b - a) * proportion
    }

    fun interpolateColor(a: Int, b: Int, proportion: Float): Int {
        val hsva = FloatArray(3)
        val hsvb = FloatArray(3)
        Color.colorToHSV(a, hsva)
        Color.colorToHSV(b, hsvb)
        for (i in 0..2) {
            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion)
        }
        return Color.HSVToColor(hsvb)
    }

    fun sort(inventory: MutableList<ItemDetails>): MutableList<ItemDetails> {
        var copy = inventory.toMutableList()
        copy.sortWith(compareBy<ItemDetails> { x ->
            if (x.invItem.quantityInStore / x.invItem.quantityInSet == 1)
                1
            else
                -1
        }.thenComparator { x, y ->
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB && y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 0
            }
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 1
            }
            if (y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator -1
            }
            val xInv = x.invItem
            val yInv = y.invItem
            yInv.lackingParts() - xInv.lackingParts()
        }.thenComparator { x, y ->
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB && y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 0
            }
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 1
            }
            if (y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator -1
            }
            x.item.name.compareTo(y.item.name)
        })
        return copy
    }

}