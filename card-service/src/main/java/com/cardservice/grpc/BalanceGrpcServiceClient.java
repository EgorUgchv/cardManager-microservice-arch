package com.cardservice.grpc;

import balance.BalanceServiceGrpc;
import balance.CardBalanceResponse;
import balance.CreateCardBalanceRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BalanceGrpcServiceClient {
    private final BalanceServiceGrpc.BalanceServiceBlockingStub blockingStub;

    public BalanceGrpcServiceClient(
            @Value("${balance.service.adress:localhost}") String serverAddress,
            @Value("${balance.service.grpc.port:9001}") int serverPort
    ) {
        log.info("Connecting to Balance Service GRPC service at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,
                serverPort).usePlaintext().build();

        blockingStub = BalanceServiceGrpc.newBlockingStub(channel);
    }

    public CardBalanceResponse createCardBalance(String cardNumber, Long balanceAmount) {
        CreateCardBalanceRequest request = CreateCardBalanceRequest.newBuilder()
                .setCardNumber(cardNumber)
                .setBalanceAmount(balanceAmount)
                .build();
        CardBalanceResponse response = blockingStub.createCardBalance(request);
        log.info("Received response from balance service via GRPC: {}" , response);
        return response;
    }
}
