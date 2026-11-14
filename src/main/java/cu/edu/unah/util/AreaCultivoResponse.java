package cu.edu.unah.util;

import cu.edu.unah.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

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
    Boolean activo;
    List<String> agroquimicos;

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
                .agroquimicos(areaCultivo.getAgroquimicos().stream().map(Agroquimico::getNombre).collect(Collectors.toList()))
                .build();
    }

    public static AreaCultivo map(AreaCultivoResponse areaCultivoResponse, Area area, Cultivo cultivo, List<Agroquimico> agroquimicos){
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
                .agroquimicos(agroquimicos)
                .cultivo(cultivo)
                .area(area)
                .build();
    }
}
