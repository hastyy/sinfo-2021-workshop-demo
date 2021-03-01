package com.example.dealers;

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
public class DealersApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealersApplication.class, args);
    }

    private final Map<Long, AtomicInteger> requestsPerSecond = new ConcurrentHashMap<>();

    @GetMapping("/api/v1/dealers")
    public Flux<Dealer> dealers() {
        return Flux.fromStream(Stream.of(
                Dealer.builder()
                    .gsid("GS000123456")
                    .legalName("C Santos")
                    .functions(List.of(
                            Function.builder()
                                .brand("MB")
                                .productGroup("Passanger Cars")
                                .service("Sales")
                                .build()
                    ))
                    .build(),
                Dealer.builder()
                        .gsid("GS291744934")
                        .legalName("Auto-viação Micaelense")
                        .functions(List.of(
                            Function.builder()
                                .brand("Smart")
                                .productGroup("Passanger Cars")
                                .service("Sales")
                                .build(),
                            Function.builder()
                                    .brand("MB")
                                    .productGroup("Passanger Cars")
                                    .service("Repair")
                                    .build()
                        ))
                        .build()
        ));
    }

    @GetMapping("/api/v1/dealers/count")
    public Flux<WithRequestCount<List<Dealer>>> countRequestsPerSecond() {
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

        return Flux.just(new WithRequestCount<List<Dealer>>(countForTheCurrentSecond, List.of(
                Dealer.builder()
                        .gsid("GS000123456")
                        .legalName("C Santos")
                        .functions(List.of(
                                Function.builder()
                                        .brand("MB")
                                        .productGroup("Passanger Cars")
                                        .service("Sales")
                                        .build()
                        ))
                        .build(),
                Dealer.builder()
                        .gsid("GS291744934")
                        .legalName("Auto-viação Micaelense")
                        .functions(List.of(
                                Function.builder()
                                        .brand("Smart")
                                        .productGroup("Passanger Cars")
                                        .service("Sales")
                                        .build(),
                                Function.builder()
                                        .brand("MB")
                                        .productGroup("Passanger Cars")
                                        .service("Repair")
                                        .build()
                        ))
                        .build())));
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Dealer {

    private String gsid;
    private String legalName;
    private Collection<Function> functions;

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Function {

    private String brand;
    private String service;
    private String productGroup;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WithRequestCount<T> {

    private Integer count;
    private T document;

}
