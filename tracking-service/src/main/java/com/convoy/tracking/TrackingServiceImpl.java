package com.convoy.tracking;

import com.convoy.tracking.repository.LocationRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class TrackingServiceImpl extends com.convoy.tracking.grpc.TrackingServiceGrpc.TrackingServiceImplBase {

    private final LocationRepository locationRepository;

    public TrackingServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public StreamObserver<com.convoy.tracking.grpc.LocationUpdate> streamLocation(StreamObserver<com.convoy.tracking.grpc.LocationAck> responseObserver){
        return new StreamObserver<com.convoy.tracking.grpc.LocationUpdate>() {

            @Override
            public void onNext(com.convoy.tracking.grpc.LocationUpdate update) {
                String driver_id = update.getDriverId() ;
                double latitude = update.getLatitude();
                double longitude = update.getLongitude();
                long timestamp = update.getTimestamp() > 0 ? update.getTimestamp() : System.currentTimeMillis();

                locationRepository.saveLocation(driver_id,latitude,longitude, timestamp);

                System.out.println("Driver Id: " + driver_id + " | Latitude: " + latitude + " | Longitude: " + longitude + " | Time: " + timestamp);
            }

            @Override
            public void onError(Throwable t) {
                IO.println("Unexpected Error " + t);
            }

            @Override
            public void onCompleted() {
                com.convoy.tracking.grpc.LocationAck ack = com.convoy.tracking.grpc.LocationAck.newBuilder()
                        .setReceived(true)
                        .setMessage("Success Location Received")
                        .build();
                responseObserver.onNext(ack);
                responseObserver.onCompleted();
            }
        };
    }

}
