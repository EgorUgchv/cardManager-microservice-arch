package com.balanceservice.grpc;

import balance.BalanceServiceGrpc;
import balance.CardBalanceResponse;
import com.balanceservice.dto.CardBalanceDto;
import com.balanceservice.mapper.CardBalanceMapper;
import com.balanceservice.service.CardBalanceService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@AllArgsConstructor
public class BalanceGrpcService extends BalanceServiceGrpc.BalanceServiceImplBase {
    private final CardBalanceMapper cardBalanceMapper;
private final CardBalanceService cardBalanceService;
    @Override
    public void createCardBalance(balance.CreateCardBalanceRequest balanceRequest,
                                  StreamObserver<balance.CardBalanceResponse> responseObserver) {
        log.info("CreateCardBalance request received {}", balanceRequest.toString());

        CardBalanceDto cardBalanceDto = cardBalanceMapper.mapToCardBalanceDto(balanceRequest);
       int balanceId  = cardBalanceService.createBalance(cardBalanceDto);

        CardBalanceResponse.Builder response = CardBalanceResponse.newBuilder()
                .setBalanceId(balanceId);
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

}
