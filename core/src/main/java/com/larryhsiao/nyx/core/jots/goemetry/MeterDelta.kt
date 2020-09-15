package com.larryhsiao.nyx.core.jots.goemetry

import com.silverhetch.clotho.Source

/**
 * Source to build a longitude/latitude value from given amount of meter.
 * Approximate Metric Equivalents for Degrees, Minutes, and Seconds
 *
 *
 *
 * At the equator for longitude and for latitude anywhere,
 * the following approximations are valid:
 *
 *
 * 1° = 111 km  (or 60 nautical miles) <br></br>
 * 0.1° = 11.1 km <br></br>
 * 0.01° = 1.11 km (2 decimals, km accuracy) <br></br>
 * 0.001° =111 m <br></br>
 * 0.0001° = 11.1 m <br></br>
 * 0.00001° = 1.11 m <br></br>
 * 0.000001° = 0.11 m (7 decimals, cm accuracy) <br></br>
 *
 *
 * <br></br>
 * 1' = 1.85 km  (or 1 nautical mile) <br></br>
 * 0.1' = 185 m <br></br>
 * 0.01' = 18.5 m <br></br>
 * 0.001' = 1.85 m <br></br>
 *
 *
 * <br></br>
 * 30" = 900 m <br></br>
 * 15" = 450 m <br></br>
 * 3" = 90 m <br></br>
 * 1" = 30 m <br></br>
 * 1/3" = 10 m <br></br>
 * 0.1" = 3 m <br></br>
 * 1/9" = 3 m <br></br>
 * 1/27" = 1 m <br></br>
 *
 *
 * If you report a position to the nearest 0.01°  (two decimal places for latitude and longitude),
 * you will only be accurate to around 1 km.
 * While this is adequate for rough locations on a global scale,
 * for detailed work it will be inadequate.
 *
 * @see [Reference](https://www.usna.edu/Users/oceano/pguth/md_help/html/approx_equivalents.htm)
 */
class MeterDelta @JvmOverloads constructor(private val meters: Double = 1.0) : Source<Double> {
    override fun value(): Double {
        return 0.00001 * meters
    }
}