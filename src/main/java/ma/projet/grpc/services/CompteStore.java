package ma.projet.grpc.services;
import ma.projet.grpc.stubs.*;
import java.util.List;
/** Frontière commune aux étapes en mémoire et JPA du TP. */
public interface CompteStore {
    List<Compte> findAllComptes();
    Compte findCompteById(String id);
    Compte saveCompte(CompteRequest request);
}
