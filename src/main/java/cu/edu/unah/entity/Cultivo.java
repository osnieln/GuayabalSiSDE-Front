package cu.edu.unah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String descripcion;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "produccionid")
    Produccion produccion;
}
