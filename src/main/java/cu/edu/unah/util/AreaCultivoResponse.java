package cu.edu.unah.util;

import cu.edu.unah.entity.Area;
import cu.edu.unah.entity.AreaCultivo;
import cu.edu.unah.entity.AreaCultivoPk;
import cu.edu.unah.entity.Cultivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaCultivoResponse implements Serializable {

    AreaCultivoResponsePK areaCultivoResponsePK;
    String fechaRecogida;
    Long planProd;
    Double prodCultivosPermanente;
    Double prodCultivosTemporales;
    Double produccionReal;

    public static AreaCultivoResponse map(AreaCultivo areaCultivo){
        return AreaCultivoResponse.builder()
                .areaCultivoResponsePK(AreaCultivoResponsePK.builder()
                        .areaId(areaCultivo.getAreaCultivoPk().getAreaId())
                        .cultivoId(areaCultivo.getAreaCultivoPk().getCultivoId())
                         .fechaSiembra(DateFormatter.format(areaCultivo.getAreaCultivoPk().getFechaSiembra()))
                        .build())
                .fechaRecogida(DateFormatter.format(areaCultivo.getFechaRecogida()))
                .planProd(areaCultivo.getPlanProd())
                .prodCultivosPermanente(areaCultivo.getProdCultivosPermanente())
                .prodCultivosTemporales(areaCultivo.getProdCultivosTemporales())
                .produccionReal(areaCultivo.getProduccionReal())
                .build();
    }

    public static AreaCultivo map(AreaCultivoResponse areaCultivoResponse, Area area, Cultivo cultivo){
        return AreaCultivo.builder()
                .areaCultivoPk(AreaCultivoPk.builder()
                        .areaId(areaCultivoResponse.areaCultivoResponsePK.getAreaId())
                        .cultivoId(areaCultivoResponse.areaCultivoResponsePK.cultivoId)
                        .fechaSiembra(DateFormatter.format(areaCultivoResponse.areaCultivoResponsePK.getFechaSiembra()))
                        .build())
                .fechaRecogida(DateFormatter.format(areaCultivoResponse.fechaRecogida))
                .planProd(areaCultivoResponse.getPlanProd())
                .prodCultivosPermanente(areaCultivoResponse.prodCultivosPermanente)
                .prodCultivosTemporales(areaCultivoResponse.prodCultivosTemporales)
                .produccionReal(areaCultivoResponse.getProduccionReal())
                .cultivo(cultivo)
                .area(area)
                .build();
    }
}
