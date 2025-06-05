package cu.edu.unah.util;

import cu.edu.unah.entity.AreaCultivoPk;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaCultivoResponsePK {
    Long areaId;
    Long cultivoId;
    String fechaSiembra;

    public static AreaCultivoResponsePK map (AreaCultivoPk areaCultivoPk){
        return AreaCultivoResponsePK.builder()
                .areaId(areaCultivoPk.getAreaId())
                .cultivoId(areaCultivoPk.getCultivoId())
                .fechaSiembra(DateFormatter.format(areaCultivoPk.getFechaSiembra()))
                .build();
    }

    public static AreaCultivoPk map (AreaCultivoResponsePK areaCultivoResponsePK){
        return AreaCultivoPk.builder()
                .areaId(areaCultivoResponsePK.areaId)
                .cultivoId(areaCultivoResponsePK.cultivoId)
                .fechaSiembra(DateFormatter.format(areaCultivoResponsePK.fechaSiembra))
                .build();
    }
}
