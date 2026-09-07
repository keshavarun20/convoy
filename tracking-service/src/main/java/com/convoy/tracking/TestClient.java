package com.convoy.tracking;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class TestClient {

    void main(String[] args) throws InterruptedException {
       Channel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        com.convoy.tracking.grpc.TrackingServiceGrpc.TrackingServiceStub stub = com.convoy.tracking.grpc.TrackingServiceGrpc.newStub(channel);

        StreamObserver<com.convoy.tracking.grpc.LocationUpdate> requestObserver = stub.streamLocation(
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

        for (int i = 0; i < 3; i++) {
            com.convoy.tracking.grpc.LocationUpdate ping = com.convoy.tracking.grpc.LocationUpdate.newBuilder()
                    .setDriverId("driver-123")
                    .setLatitude(6.9271 + i * 0.001)
                    .setLongitude(79.8612 + i * 0.001)
                    .setTimestamp(System.currentTimeMillis())
                    .build();

            requestObserver.onNext(ping);
            Thread.sleep(500); // small gap so it feels like real pings over time
        }
        requestObserver.onCompleted();

        Thread.sleep(2000);
    }
}
