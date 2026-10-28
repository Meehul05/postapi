
package com.example.postapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    // Producer factory with custom Jackson serialization
    @Bean
    public ProducerFactory<String, EventMessage> producerFactory() {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> config = new HashMap<>();
        config.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            BOOTSTRAP_SERVERS
        );

        Serializer<String> keySerializer = new StringSerializer();

        Serializer<EventMessage> valueSerializer = new Serializer<>() {
            @Override
            public byte[] serialize(String topic, EventMessage event) {
                if (event == null) {
                    return null;
                }

                try {
                    return objectMapper.writeValueAsBytes(event);
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Failed to serialize event", e
                    );
                }
            }
        };

        return new DefaultKafkaProducerFactory<>(
            config,
            keySerializer,
            valueSerializer
        );
    }

    // Kafka template
    @Bean
    public KafkaTemplate<String, EventMessage> kafkaTemplate(
            ProducerFactory<String, EventMessage> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Consumer factory with custom Jackson deserialization
    @Bean
    public ConsumerFactory<String, EventMessage> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            BOOTSTRAP_SERVERS
        );
        config.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            "postapi-group"
        );
        config.put(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"
        );

        return new DefaultKafkaConsumerFactory<>(
            config,
            new StringDeserializer(),
            new EventMessageDeserializer()
        );
    }

    // Dead-letter publishing and exponential retry
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, EventMessage> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(
                (KafkaOperations<String, EventMessage>) kafkaTemplate,
                (record, exception) -> new TopicPartition(
                    record.topic() + ".DLT",
                    record.partition()
                )
            );

        // Initial delay: 1 second; multiplier: 2
        ExponentialBackOff backOff =
            new ExponentialBackOff(1000L, 2.0);

        backOff.setMaxElapsedTime(5000L);

        return new DefaultErrorHandler(recoverer, backOff);
    }

    // Kafka listener container factory
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, EventMessage>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, EventMessage> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, EventMessage> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);

        return factory;
    }
}
