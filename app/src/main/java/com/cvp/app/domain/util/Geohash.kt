package com.cvp.app.domain.util

/**
 * Pure geohash encode/decode/neighbors implementation.
 * Coordinate order follows geographic convention: latitude first, longitude second.
 * Base32 alphabet: "0123456789bcdefghjkmnpqrstuvwxyz"
 */
object Geohash {

    private const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"

    /**
     * Encodes [lat]/[lon] to a geohash string of the given [precision] (1–12 characters).
     * Boundary coordinates are assigned to the upper (right/north) half of their range.
     */
    fun encode(lat: Double, lon: Double, precision: Int = 6): String {
        require(precision in 1..12) { "precision must be 1–12, was $precision" }
        var minLat = -90.0; var maxLat = 90.0
        var minLon = -180.0; var maxLon = 180.0
        val result = StringBuilder(precision)
        var bits = 0
        var charBits = 0
        var isLon = true

        while (result.length < precision) {
            if (isLon) {
                val mid = (minLon + maxLon) / 2.0
                if (lon >= mid) { bits = (bits shl 1) or 1; minLon = mid }
                else { bits = bits shl 1; maxLon = mid }
            } else {
                val mid = (minLat + maxLat) / 2.0
                if (lat >= mid) { bits = (bits shl 1) or 1; minLat = mid }
                else { bits = bits shl 1; maxLat = mid }
            }
            isLon = !isLon
            charBits++
            if (charBits == 5) {
                result.append(BASE32[bits])
                charBits = 0
                bits = 0
            }
        }
        return result.toString()
    }

    /**
     * Decodes a geohash string to the center (lat, lon) of its cell.
     * Returns a [Pair] of (latitude, longitude).
     */
    fun decode(hash: String): Pair<Double, Double> {
        var minLat = -90.0; var maxLat = 90.0
        var minLon = -180.0; var maxLon = 180.0
        var isLon = true

        for (char in hash.lowercase()) {
            val idx = BASE32.indexOf(char)
            require(idx >= 0) { "Invalid geohash character: '$char'" }
            for (i in 4 downTo 0) {
                val bit = (idx shr i) and 1
                if (isLon) {
                    val mid = (minLon + maxLon) / 2.0
                    if (bit == 1) minLon = mid else maxLon = mid
                } else {
                    val mid = (minLat + maxLat) / 2.0
                    if (bit == 1) minLat = mid else maxLat = mid
                }
                isLon = !isLon
            }
        }
        return Pair((minLat + maxLat) / 2.0, (minLon + maxLon) / 2.0)
    }

    /**
     * Returns the 8 neighboring geohash cells in the order: N, S, E, W, NE, NW, SE, SW.
     */
    fun neighbors(hash: String): List<String> {
        val precision = hash.length
        val (centerLat, centerLon) = decode(hash)
        val (latH, lonW) = cellDimensions(precision)

        return listOf(
            encode((centerLat + latH).coerceIn(-90.0, 90.0), wrapLon(centerLon), precision),          // N
            encode((centerLat - latH).coerceIn(-90.0, 90.0), wrapLon(centerLon), precision),          // S
            encode(centerLat, wrapLon(centerLon + lonW), precision),                                   // E
            encode(centerLat, wrapLon(centerLon - lonW), precision),                                   // W
            encode((centerLat + latH).coerceIn(-90.0, 90.0), wrapLon(centerLon + lonW), precision),   // NE
            encode((centerLat + latH).coerceIn(-90.0, 90.0), wrapLon(centerLon - lonW), precision),   // NW
            encode((centerLat - latH).coerceIn(-90.0, 90.0), wrapLon(centerLon + lonW), precision),   // SE
            encode((centerLat - latH).coerceIn(-90.0, 90.0), wrapLon(centerLon - lonW), precision),   // SW
        )
    }

    /**
     * Returns the (latHeight, lonWidth) of a geohash cell in degrees.
     * Geohash interleaves lon (odd bit positions) and lat (even bit positions),
     * so lon gets ceil(totalBits/2) bits and lat gets floor(totalBits/2) bits.
     */
    fun cellDimensions(precision: Int): Pair<Double, Double> {
        val totalBits = precision * 5
        val lonBits = (totalBits + 1) / 2
        val latBits = totalBits / 2
        val lonWidth = 360.0 / (1L shl lonBits)
        val latHeight = 180.0 / (1L shl latBits)
        return Pair(latHeight, lonWidth)
    }

    private fun wrapLon(lon: Double): Double = ((lon + 180.0) % 360.0 + 360.0) % 360.0 - 180.0
}
