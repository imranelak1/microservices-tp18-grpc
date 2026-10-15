package ma.projet.grpc;
import ma.projet.grpc.services.MemoryCompteService;
import ma.projet.grpc.stubs.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MemoryStoreTest {
    @Test void firstStageStoresAndFindsAccounts() {
        var store=new MemoryCompteService();
        var compte=store.saveCompte(CompteRequest.newBuilder().setSolde(10).setDateCreation("2024-11-18").build());
        assertEquals(compte,store.findCompteById(compte.getId())); assertEquals(1,store.findAllComptes().size());
    }
}
