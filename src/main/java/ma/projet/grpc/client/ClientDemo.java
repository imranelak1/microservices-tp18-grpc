package ma.projet.grpc.client;
import io.grpc.ManagedChannelBuilder;
import ma.projet.grpc.stubs.*;
import java.util.concurrent.TimeUnit;
/** Client réseau utilisant les stubs générés par protoc. */
public class ClientDemo {
    public static void main(String[] args) throws Exception {
        var channel=ManagedChannelBuilder.forAddress(System.getProperty("grpc.host","localhost"),Integer.getInteger("grpc.port",9090)).usePlaintext().build();
        try {
            var stub=CompteServiceGrpc.newBlockingStub(channel).withDeadlineAfter(5,TimeUnit.SECONDS);
            var request=CompteRequest.newBuilder().setSolde(1500).setDateCreation("2024-11-18").setType(TypeCompte.COURANT).build();
            var compte=stub.saveCompte(SaveCompteRequest.newBuilder().setCompte(request).build()).getCompte();
            System.out.println("SaveCompte: "+compte);
            System.out.println("CompteById: "+stub.compteById(GetCompteByIdRequest.newBuilder().setId(compte.getId()).build()));
            System.out.println("AllComptes: "+stub.allComptes(GetAllComptesRequest.getDefaultInstance()));
            System.out.println("TotalSolde: "+stub.totalSolde(GetTotalSoldeRequest.getDefaultInstance()));
        } finally { channel.shutdownNow().awaitTermination(5,TimeUnit.SECONDS); }
    }
}
