package cu.edu.unah.util;

import cu.edu.unah.entity.Area;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaResponse {

    Long id;

    String descripcion;

    String ubicacion;

    public static Area AreaResponseToArea(AreaResponse areaResponse){
        return Area.builder()
                .id(areaResponse.getId())
                .descripcion(areaResponse.getDescripcion())
                .ubicacion((Polygon) GeometryUtil.wktToGeometry(areaResponse.ubicacion))
                .build();
    }

    public static AreaResponse AreaToAreaResponse(Area area){
        return new AreaResponse(area.getId(), area.getDescripcion(), GeometryUtil.geometryToWkt(area.getUbicacion()));
    }
}
