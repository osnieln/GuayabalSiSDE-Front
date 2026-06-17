package cu.edu.unah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agroquimico implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    private Double stockActual;

    private Double stockMinimo;

    // Relación ManyToMany con AreaCultivo
    @ManyToMany(mappedBy = "agroquimicos", fetch = FetchType.LAZY)
    private List<AreaCultivo> areaCultivos;
}