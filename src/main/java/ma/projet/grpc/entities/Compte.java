package ma.projet.grpc.entities;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
public class Compte {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
    private float solde;
    private String dateCreation;
    private String type;
}
