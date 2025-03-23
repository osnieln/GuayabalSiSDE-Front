package cu.edu.unah.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

public class GeometryUtil {

    public static final int SRID = 4326; //LatLng
    private static WKTReader wktReader = new WKTReader();
    private static WKTWriter wktwriter = new WKTWriter();

    /**
     * Esta función permite parsear un <em>String</em> que contiene un punto formado por dos coordenadas. Esta función
     * permitirá obtener un objeto de tipo {@link Geometry}.
     * @param wellKnownText Texto que contiene los datos de un punto formado por sus coordenadas.
     * @return Devuelve un objeto de tipo {@link Geometry}.
     */
    public static Geometry wktToGeometry(String wellKnownText) {
        Geometry geometry = null;

        try {
            geometry = wktReader.read(wellKnownText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return geometry;
    }

    public static String geometryToWkt(Polygon geometry) {
        return wktwriter.write(geometry);
    }
}
