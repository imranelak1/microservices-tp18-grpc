package ma.projet.grpc.services;
import ma.projet.grpc.repositories.CompteRepository;
import ma.projet.grpc.stubs.*;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Profile("!memory") @Transactional
public class CompteService implements CompteStore {
    private final CompteRepository repository;
    public CompteService(CompteRepository repository) { this.repository=repository; }
    @Override public List<Compte> findAllComptes() { return repository.findAll().stream().map(this::toProto).toList(); }
    @Override public Compte findCompteById(String id) { return repository.findById(id).map(this::toProto).orElseThrow(()->new NoSuchElementException("Compte non trouvé")); }
    @Override public Compte saveCompte(CompteRequest request) {
        validate(request);
        var compte=new ma.projet.grpc.entities.Compte();
        compte.setSolde(request.getSolde()); compte.setDateCreation(request.getDateCreation()); compte.setType(request.getType().name());
        return toProto(repository.save(compte));
    }
    private Compte toProto(ma.projet.grpc.entities.Compte entity) { return Compte.newBuilder().setId(entity.getId()).setSolde(entity.getSolde()).setDateCreation(entity.getDateCreation()).setType(TypeCompte.valueOf(entity.getType())).build(); }
    static void validate(CompteRequest request) {
        if(!Float.isFinite(request.getSolde()) || request.getSolde()<0 || request.getDateCreation().isBlank() || request.getType()==TypeCompte.UNRECOGNIZED) throw new IllegalArgumentException("Compte invalide");
        java.time.LocalDate.parse(request.getDateCreation().replace('/','-'));
    }
}
