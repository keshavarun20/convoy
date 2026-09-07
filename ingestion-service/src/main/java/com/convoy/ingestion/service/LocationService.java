package com.convoy.ingestion.service;

import com.convoy.ingestion.dto.PingRequest;
import com.convoy.tracking.grpc.TrackingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final ManagedChannel channel;
    private final TrackingServiceGrpc.TrackingServiceStub stub;


    public LocationService(
            @Value("${grpc.host:localhost}") String host,
            @Value("${grpc.port:9090}") int port) {

        // This runs exactly ONCE when the app starts
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        this.stub = TrackingServiceGrpc.newStub(channel);
    }

    public void forwardPing(PingRequest request) {
        StreamObserver<com.convoy.tracking.grpc.LocationUpdate> pingStream = stub.streamLocation(
                new StreamObserver<com.convoy.tracking.grpc.LocationAck>() {
                    @Override
                    public void onNext(com.convoy.tracking.grpc.LocationAck ack) {
                        IO.println(ack.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        IO.println(t);
                    }

                    @Override
                    public void onCompleted() { }
                }
        );

        com.convoy.tracking.grpc.LocationUpdate ping = com.convoy.tracking.grpc.LocationUpdate.newBuilder()
                .setDriverId(request.driverId())
                .setLatitude(request.latitude())
                .setLongitude(request.longitude())
                .setTimestamp(System.currentTimeMillis())
                .build();

        pingStream.onNext(ping);
        pingStream.onCompleted();
    }
}
