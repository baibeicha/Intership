package com.intership.paymentservice;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.intership.paymentservice.model.dto.PaymentRequest;
import com.intership.paymentservice.model.entity.Payment;
import com.intership.paymentservice.repository.PaymentRepository;
import com.intership.paymentservice.service.PaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PaymentServiceIntegrationTest {

    private static final int WIREMOCK_PORT = 9571;

    @RegisterExtension
    static final WireMockExtension WM = WireMockExtension.newInstance()
            .options(wireMockConfig().port(WIREMOCK_PORT))
            .build();

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("random.api.url", () -> "http://localhost:" + WIREMOCK_PORT + "/random");
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.data.mongodb.database", () -> "test");
        registry.add("spring.kafka.admin.properties.allow.auto.create.topics", () -> "true");

        registry.add("topic.create-payment", () -> "create-payment");
    }

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void beforeEach() {
        WM.resetAll();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void processPayment_whenRandomApiReturnsEven_thenPaymentSavedAndEventSent() throws Exception {
        WM.stubFor(get(urlPathEqualTo("/random"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("2")
                        .withStatus(200)));

        PaymentRequest request = new PaymentRequest(100L, 200L, new BigDecimal("12.34"));

        paymentService.processPayment(request);

        List<Payment> payments = paymentRepository.findByOrderId(100L);
        assertThat(payments).hasSize(1);

        Payment saved = payments.getFirst();
        assertThat(saved.getUserId()).isEqualTo(200L);
        assertThat(saved.getPaymentAmount()).isEqualByComparingTo(new BigDecimal("12.34"));
        assertThat(saved.getStatus()).isNotNull();

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        props.put("group.id", "test-consumer-group-" + System.currentTimeMillis());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", "30000");
        props.put("heartbeat.interval.ms", "10000");

        try (var consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(List.of("create-payment"));

            ConsumerRecords<String, String> records = null;
            for (int i = 0; i < 10; i++) {
                records = consumer.poll(Duration.ofSeconds(1));
                if (records.count() > 0) {
                    break;
                }
                Thread.sleep(500);
            }

            assertThat(records.count())
                    .overridingErrorMessage(
                            "Expected at least 1 Kafka message but got %d",
                            records.count()
                    )
                    .isGreaterThanOrEqualTo(1);
        }
    }
}