package com.transacciones.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    @Mock
    private ConnectionFactory connectionFactory;

    private RabbitMQConfig rabbitMQConfig;

    @BeforeEach
    void setUp() {
        rabbitMQConfig = new RabbitMQConfig();
    }

    @Test
    @DisplayName("queue_shouldCreateDurableQueueWithName")
    void queue_shouldCreateDurableQueueWithName() {
        // Act
        Queue queue = rabbitMQConfig.queue();

        // Assert
        assertNotNull(queue);
        assertEquals("transfer-queue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    @DisplayName("topicExchange_shouldCreateTopicExchangeWithName")
    void topicExchange_shouldCreateTopicExchangeWithName() {
        // Act
        TopicExchange exchange = rabbitMQConfig.topicExchange();

        // Assert
        assertNotNull(exchange);
        assertEquals("transfer-exchange", exchange.getName());
    }

    @Test
    @DisplayName("binding_shouldBindQueueToExchangeWithRoutingKey")
    void binding_shouldBindQueueToExchangeWithRoutingKey() {
        // Arrange
        Queue queue = new Queue("transfer-queue", true);
        TopicExchange exchange = new TopicExchange("transfer-exchange");

        // Act
        Binding binding = rabbitMQConfig.binding(queue, exchange);

        // Assert
        assertNotNull(binding);
        assertEquals("transfer-queue", binding.getDestination());
        assertEquals("transfer-exchange", binding.getExchange());
        assertEquals("transfer-routing-key", binding.getRoutingKey());
    }

    @Test
    @DisplayName("messageConverter_shouldReturnJackson2JsonMessageConverter")
    void messageConverter_shouldReturnJackson2JsonMessageConverter() {
        // Act
        MessageConverter converter = rabbitMQConfig.messageConverter();

        // Assert
        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);
    }

    @Test
    @DisplayName("rabbitTemplate_shouldBeConfiguredWithJsonConverter")
    void rabbitTemplate_shouldBeConfiguredWithJsonConverter() {
        // Act
        RabbitTemplate template = rabbitMQConfig.rabbitTemplate(connectionFactory);

        // Assert
        assertNotNull(template);
        assertEquals(connectionFactory, template.getConnectionFactory());
        assertInstanceOf(Jackson2JsonMessageConverter.class, template.getMessageConverter());
    }
}
