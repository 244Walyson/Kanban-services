package com.kanban.chat.configs.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaConfig {

    private static final int PARTITION_COUNT = 1;
    private static final short REPLICA_COUNT = 1;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${spring.kafka.consumer.team.save.success.topic}")
    private String teamSaveSuccessTopic;
    @Value("${spring.kafka.add.user.topic}")
    private String addUserTopic;
    @Value("${spring.kafka.user.connection.topic}")
    private String userConnectionTopic;
    @Value("${spring.kafka.user.notification.topic}")
    private String userNotificationTopic;
    @Value("${spring.kafka.user.chat-created-notification.topic}")
    private String chatCreatedNotificationTopic;
    @Value("${spring.kafka.user.message-notification.topic}")
    private String messageNotificationTopic;
    @Value("${spring.kafka.sasl.username}")
    private String saslUsername;
    @Value("${spring.kafka.sasl.password}")
    private String saslPassword;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        Map props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        log.info("SSSSSSSSSSSSSSAAAAAAAAAAAAAAAAAAASSSSSSSSSSLLLLLLLLLLLLLLLLLLLLL");

        var jaasConfig = String.format("%s required username=\"%s\" " + "password=\"%s\";", PlainLoginModule.class.getName(), saslUsername, saslPassword);
        log.info("SASL JAAS config: {}", jaasConfig);
        log.info("SASL username: {}", saslUsername);
        log.info("SASL password: {}", saslPassword);

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    private Map<String, Object> producerProps() {
        var props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                "%s required username=\"%s\" " + "password=\"%s\";", PlainLoginModule.class.getName(), saslUsername, saslPassword
        ));
        return props;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    private NewTopic buildTopic(String name) {
        return TopicBuilder
                .name(name)
                .replicas(PARTITION_COUNT)
                .partitions(REPLICA_COUNT)
                .build();
    }

    @Bean
    public NewTopic teamSaveSuccessTopic() {
        return buildTopic(teamSaveSuccessTopic);
    }

    @Bean
    public NewTopic addUserTopic() {
        return buildTopic(addUserTopic);
    }
    @Bean
    public NewTopic addUserConnTopic() {
        return buildTopic(userConnectionTopic);
    }
    @Bean
    public NewTopic addUserNotification() {
        return buildTopic(userNotificationTopic);
    }
    @Bean
    public NewTopic addChatCreatedNotification() { return buildTopic(chatCreatedNotificationTopic); }
    @Bean
    public NewTopic addMessageNotification() { return buildTopic(messageNotificationTopic); }

}
