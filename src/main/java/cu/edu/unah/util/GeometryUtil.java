package cu.edu.unah.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String convertMultiPolygonToPolygon(String multiPolygon) {
        // Patrón para extraer las coordenadas del MULTIPOLYGON
        Pattern pattern = Pattern.compile("MULTIPOLYGON\\(\\((.+?)\\)\\)");
        Matcher matcher = pattern.matcher(multiPolygon);

        if (matcher.find()) {
            String coordinates = matcher.group(1);
            // Eliminar paréntesis adicionales y espacios innecesarios
            coordinates = coordinates.replaceAll("[\\(\\)]", "").trim();
            // Añadir espacios después de las comas entre coordenadas
            coordinates = coordinates.replace(",", ", ");
            // Construir el POLYGON
            return "POLYGON((" + coordinates + "))";
        } else {
            throw new IllegalArgumentException("Formato MULTIPOLYGON no válido");
        }
    }
}
