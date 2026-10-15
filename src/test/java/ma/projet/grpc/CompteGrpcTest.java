package ma.projet.grpc;
import ma.projet.grpc.stubs.*;
import ma.projet.grpc.repositories.CompteRepository;
import io.grpc.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import java.util.concurrent.TimeUnit;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompteGrpcTest {
    static final int GRPC_PORT=freePort();
    static int freePort() { try(var socket=new java.net.ServerSocket(0)) {return socket.getLocalPort();} catch(java.io.IOException e){throw new IllegalStateException(e);} }
    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {registry.add("grpc.server.port",()->GRPC_PORT);}
    @Autowired CompteRepository repository;
    ManagedChannel channel;
    CompteServiceGrpc.CompteServiceBlockingStub stub;
    @BeforeEach void setup() { repository.deleteAll();channel=ManagedChannelBuilder.forAddress("localhost",GRPC_PORT).usePlaintext().build();stub=CompteServiceGrpc.newBlockingStub(channel).withDeadlineAfter(5,TimeUnit.SECONDS); }
    @AfterEach void stop() { channel.shutdownNow(); }
    Compte save(float solde) { return stub.saveCompte(SaveCompteRequest.newBuilder().setCompte(CompteRequest.newBuilder().setSolde(solde).setDateCreation("2024-11-18").setType(TypeCompte.COURANT)).build()).getCompte(); }
    @Test void saveAndFindViaNetwork() { var compte=save(1500);assertFalse(compte.getId().isEmpty());assertEquals(compte,stub.compteById(GetCompteByIdRequest.newBuilder().setId(compte.getId()).build()).getCompte()); assertEquals(1,repository.count()); }
    @Test void listContainsPersistedAccounts() { save(100);save(200);assertEquals(2,stub.allComptes(GetAllComptesRequest.getDefaultInstance()).getComptesCount()); }
    @Test void statisticsAreCorrect() { save(100);save(200);var stats=stub.totalSolde(GetTotalSoldeRequest.getDefaultInstance()).getStats();assertEquals(2,stats.getCount());assertEquals(300,stats.getSum());assertEquals(150,stats.getAverage()); }
    @Test void emptyAverageIsZero() { assertEquals(0,stub.totalSolde(GetTotalSoldeRequest.getDefaultInstance()).getStats().getAverage()); }
    @Test void missingAccountReturnsNotFound() { var error=assertThrows(StatusRuntimeException.class,()->stub.compteById(GetCompteByIdRequest.newBuilder().setId("absent").build()));assertEquals(Status.Code.NOT_FOUND,error.getStatus().getCode()); }
    @Test void negativeBalanceIsInvalidArgument() { var error=assertThrows(StatusRuntimeException.class,()->save(-1));assertEquals(Status.Code.INVALID_ARGUMENT,error.getStatus().getCode()); assertEquals(0,repository.count()); }
    @Test void protobufRoundTripPreservesEnumAndFields() throws Exception { var compte=Compte.newBuilder().setId("42").setSolde(20).setDateCreation("2024-11-18").setType(TypeCompte.EPARGNE).build();assertEquals(compte,Compte.parseFrom(compte.toByteArray())); }
}
