package cu.edu.unah.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaCultivoPk implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Long areaId;
    Long cultivoId;
    Date fechaSiembra;
}
