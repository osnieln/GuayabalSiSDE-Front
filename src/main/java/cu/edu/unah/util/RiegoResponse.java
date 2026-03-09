package cu.edu.unah.util;

import cu.edu.unah.entity.AreaCultivo;
import cu.edu.unah.entity.Riego;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiegoResponse {

    Long id;
    AreaCultivoResponsePK areaCultivoResponsePk;
    String fechaPlanificacion;
    String fechaReal;
    String advertencia;

    public static RiegoResponse map(Riego riego){
        return RiegoResponse.builder()
                .id(riego.getId())
                .areaCultivoResponsePk(AreaCultivoResponsePK.map(riego.getAreaCultivo().getAreaCultivoPk()))
                .fechaPlanificacion(DateFormatter.format(riego.getFechaPlanificacion()))
                .fechaReal(DateFormatter.format(riego.getFechaReal()))
                .build();
    }

    public static Riego map(RiegoResponse riegoResponse, AreaCultivo areaCultivo){
        return Riego.builder()
                .id(riegoResponse.id)
                .areaCultivo(areaCultivo)
                .fechaPlanificacion(DateFormatter.format(riegoResponse.getFechaPlanificacion()))
                .fechaReal(DateFormatter.format(riegoResponse.getFechaReal()))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiegoResponse that = (RiegoResponse) o;
        return (Objects.equals(fechaPlanificacion, that.fechaPlanificacion)) &&
                (Objects.equals(fechaReal, that.fechaReal));
    }


    @Override
    public int hashCode() {
        int result = fechaPlanificacion != null ? fechaPlanificacion.hashCode() : 0;
        result = 31 * result + (fechaReal != null ? fechaReal.hashCode() : 0);
        return result;
    }
}
