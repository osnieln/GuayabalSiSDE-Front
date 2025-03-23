package cu.edu.unah.util;

import cu.edu.unah.entity.AreaCultivo;
import cu.edu.unah.entity.AreaCultivoPk;
import cu.edu.unah.entity.Cultivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaCultivoResponse implements Serializable {

    AreaCultivoPk areaCultivoPk;
    Date fechaRecogida;
    Long planProd;
    Double prodCultivosPermanente;
    Double prodCultivosTemporales;
    Double produccionReal;

    private Cultivo cultivo;

    private AreaResponse areaResponse;

    public static AreaCultivoResponse AreaCultivoToAreaCultivoResponse(AreaCultivo areaCultivo){
        return new AreaCultivoResponse(
                areaCultivo.getAreaCultivoPk(),
                areaCultivo.getFechaRecogida(),
                areaCultivo.getPlanProd(),
                areaCultivo.getProdCultivosPermanente(),
                areaCultivo.getProdCultivosTemporales(),
                areaCultivo.getProduccionReal(),
                areaCultivo.getCultivo() == null ? null : areaCultivo.getCultivo(),
                areaCultivo.getArea() == null ? null : AreaResponse.AreaToAreaResponse(areaCultivo.getArea())
                );
    }

    public static AreaCultivo AreaCultivoResponseAreaCultivo(AreaCultivoResponse areaCultivoResponse){
        return AreaCultivo.builder()
                .areaCultivoPk(areaCultivoResponse.areaCultivoPk)
                .fechaRecogida(areaCultivoResponse.getFechaRecogida())
                .planProd(areaCultivoResponse.getPlanProd())
                .prodCultivosPermanente(areaCultivoResponse.prodCultivosPermanente)
                .prodCultivosTemporales(areaCultivoResponse.prodCultivosTemporales)
                .produccionReal(areaCultivoResponse.getProduccionReal())
                .cultivo(areaCultivoResponse.cultivo == null ? null : areaCultivoResponse.cultivo)
                .area(areaCultivoResponse.areaResponse == null ? null : AreaResponse.AreaResponseToArea(areaCultivoResponse.areaResponse))
                .build();
    }
}
