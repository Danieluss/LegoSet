package me.danieluss.ubiquitous_systems2

import android.graphics.Color
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

}