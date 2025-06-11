package com.cardservice.grpc;

import balance.BalanceServiceGrpc;
import balance.CardBalanceResponse;
import balance.CreateCardBalanceRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@Slf4j
public class BalanceGrpcServiceClient {
    private final BalanceServiceGrpc.BalanceServiceBlockingStub blockingStub;
    private final BalanceServiceGrpc.BalanceServiceStub asyncStub;

    public BalanceGrpcServiceClient(
            @Value("${balance.service.adress:localhost}") String serverAddress,
            @Value("${balance.service.grpc.port:9001}") int serverPort
    ) {
        log.info("Connecting to Balance Service GRPC service at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,
                serverPort).usePlaintext().enableRetry().keepAliveTime(10, TimeUnit.SECONDS).build();

        blockingStub = BalanceServiceGrpc.newBlockingStub(channel);
        asyncStub = BalanceServiceGrpc.newStub(channel);
    }

//    public CardBalanceResponse createCardBalance(String cardNumber, Long balanceAmount) {
//        CreateCardBalanceRequest request = CreateCardBalanceRequest.newBuilder()
//                .setCardNumber(cardNumber)
//                .setBalanceAmount(balanceAmount)
//                .build();
//        CardBalanceResponse response = blockingStub.createCardBalance(request);
//        log.info("Received response from balance service via GRPC: {}" , response);
//        return response;
//    }

    public void createCardBalanceAsync(String cardNumber, Long balanceAmount, Consumer<CardBalanceResponse> callback) {
        CreateCardBalanceRequest request = CreateCardBalanceRequest.newBuilder()
                .setCardNumber(cardNumber)
                .setBalanceAmount(balanceAmount)
                .build();
        asyncStub.createCardBalance(request, new StreamObserver<CardBalanceResponse>() {
            @Override
            public void onNext(CardBalanceResponse response) {
                log.info("Received response from balance service via GRPC: {}", response);
                callback.accept(response);

            }

            @Override
            public void onError(Throwable t) {
                log.error("Error occurred while creating balance in balance service", t);
                throw new RuntimeException(t);
            }

            @Override
            public void onCompleted() {
                log.info("Card balance successfully created in balance service");
            }
        });
    }
}
