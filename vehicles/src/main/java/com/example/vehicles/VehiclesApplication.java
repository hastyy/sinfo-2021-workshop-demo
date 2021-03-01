package com.example.vehicles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RestController
@SpringBootApplication
public class VehiclesApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehiclesApplication.class, args);
    }

    private final Map<Long, AtomicInteger> requestsPerSecond = new ConcurrentHashMap<>();

    @GetMapping("/api/v1/vehicles")
    public Flux<Vehicle> dealers() {
        return Flux.fromStream(Stream.of(
                Vehicle.builder()
                        .vin("JH4DA175XGS009825")
                        .name("Mercedes-Benz Class A 180d")
                        .dealers(List.of("GS000123456"))
                        .build(),
                Vehicle.builder()
                        .vin("RW4DA175XGS009123")
                        .name("Smart Forfour")
                        .dealers(List.of("GS291744934", "GS000123456"))
                        .build()
        ));
    }

    @GetMapping("/api/v1/vehicles/count")
    public Flux<WithRequestCount<List<Vehicle>>> countRequestsPerSecond() {
        var now = System.currentTimeMillis();
        var second = (long)(now / 1000);
        var countForTheCurrentSecond = this.requestsPerSecond.compute(second, (aLong, atomicInteger) -> {
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
            }
            atomicInteger.incrementAndGet();
            return atomicInteger;
        }).get();

        System.out.println(String.format("There have been %d request for the second %d", countForTheCurrentSecond, second));

        return Flux.just(new WithRequestCount<List<Vehicle>>(countForTheCurrentSecond, List.of(
                Vehicle.builder()
                        .vin("JH4DA175XGS009825")
                        .name("Mercedes-Benz Class A 180d")
                        .dealers(List.of("GS000123456"))
                        .build(),
                Vehicle.builder()
                        .vin("RW4DA175XGS009123")
                        .name("Smart Forfour")
                        .dealers(List.of("GS291744934", "GS000123456"))
                        .build())));
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Vehicle {

    private String vin;
    private String name;
    private Collection<String> dealers;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WithRequestCount<T> {

    private Integer count;
    private T document;

}