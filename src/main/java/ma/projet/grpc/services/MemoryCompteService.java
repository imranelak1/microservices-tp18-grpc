package ma.projet.grpc.services;
import ma.projet.grpc.stubs.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/** Première étape du TP : activer avec --spring.profiles.active=memory. */
@Service @Profile("memory")
public class MemoryCompteService implements CompteStore {
    private final Map<String,Compte> comptes=new ConcurrentHashMap<>();
    public List<Compte> findAllComptes() { return new ArrayList<>(comptes.values()); }
    public Compte findCompteById(String id) { var compte=comptes.get(id); if(compte==null) throw new NoSuchElementException("Compte non trouvé"); return compte; }
    public Compte saveCompte(CompteRequest request) {
        CompteService.validate(request);
        var compte=Compte.newBuilder().setId(UUID.randomUUID().toString()).setSolde(request.getSolde()).setDateCreation(request.getDateCreation()).setType(request.getType()).build();
        comptes.put(compte.getId(),compte); return compte;
    }
}
