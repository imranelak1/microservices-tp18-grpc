package ma.projet.grpc.controllers;
import ma.projet.grpc.stubs.*;
import ma.projet.grpc.services.CompteStore;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.*;
import java.util.function.Supplier;
/** Adaptateur gRPC : une erreur termine le flux, sans onCompleted après onError. */
@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {
    private final CompteStore comptes;
    public CompteServiceImpl(CompteStore comptes) { this.comptes=comptes; }
    @Override public void allComptes(GetAllComptesRequest request,StreamObserver<GetAllComptesResponse> out) {
        reply(out,()->GetAllComptesResponse.newBuilder().addAllComptes(comptes.findAllComptes()).build());
    }
    @Override public void compteById(GetCompteByIdRequest request,StreamObserver<GetCompteByIdResponse> out) {
        reply(out,()->GetCompteByIdResponse.newBuilder().setCompte(comptes.findCompteById(request.getId())).build());
    }
    @Override public void saveCompte(SaveCompteRequest request,StreamObserver<SaveCompteResponse> out) {
        reply(out,()->SaveCompteResponse.newBuilder().setCompte(comptes.saveCompte(request.getCompte())).build());
    }
    @Override public void totalSolde(GetTotalSoldeRequest request,StreamObserver<GetTotalSoldeResponse> out) {
        reply(out,()->{
            var all=comptes.findAllComptes(); float sum=0; for(var compte:all) sum+=compte.getSolde();
            var stats=SoldeStats.newBuilder().setCount(all.size()).setSum(sum).setAverage(all.isEmpty()?0:sum/all.size()).build();
            return GetTotalSoldeResponse.newBuilder().setStats(stats).build();
        });
    }
    private <T> void reply(StreamObserver<T> out,Supplier<T> action) {
        try { out.onNext(action.get()); out.onCompleted(); }
        catch(NoSuchElementException e) { out.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()); }
        catch(IllegalArgumentException e) { out.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()); }
        catch(Exception e) { out.onError(Status.INTERNAL.withDescription("Erreur de persistance").asRuntimeException()); }
    }
}
