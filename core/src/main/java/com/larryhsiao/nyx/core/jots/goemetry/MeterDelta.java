package com.larryhsiao.nyx.core.jots.goemetry;

import com.silverhetch.clotho.Source;

/**
 * Source to build a longitude/latitude value from given amount of meter.
 * Approximate Metric Equivalents for Degrees, Minutes, and Seconds
 * </br>
 * <p>
 * At the equator for longitude and for latitude anywhere,
 * the following approximations are valid:
 * <p>
 * 1° = 111 km  (or 60 nautical miles) <br>
 * 0.1° = 11.1 km <br>
 * 0.01° = 1.11 km (2 decimals, km accuracy) <br>
 * 0.001° =111 m <br>
 * 0.0001° = 11.1 m <br>
 * 0.00001° = 1.11 m <br>
 * 0.000001° = 0.11 m (7 decimals, cm accuracy) <br>
 * <p>
 * <br>
 * 1' = 1.85 km  (or 1 nautical mile) <br>
 * 0.1' = 185 m <br>
 * 0.01' = 18.5 m <br>
 * 0.001' = 1.85 m <br>
 * <p>
 * <br>
 * 30" = 900 m <br>
 * 15" = 450 m <br>
 * 3" = 90 m <br>
 * 1" = 30 m <br>
 * 1/3" = 10 m <br>
 * 0.1" = 3 m <br>
 * 1/9" = 3 m <br>
 * 1/27" = 1 m <br>
 * <p>
 * If you report a position to the nearest 0.01°  (two decimal places for latitude and longitude),
 * you will only be accurate to around 1 km.
 * While this is adequate for rough locations on a global scale,
 * for detailed work it will be inadequate.
 *
 * @see <a href="https://www.usna.edu/Users/oceano/pguth/md_help/html/approx_equivalents.htm">Reference</a>
 */
public class MeterDelta implements Source<Double> {
    private final double meters;

    public MeterDelta() {
        this(1.0);
    }

    public MeterDelta(double meters) {this.meters = meters;}

    @Override
    public Double value() {
        return 0.0001;
    }
}
