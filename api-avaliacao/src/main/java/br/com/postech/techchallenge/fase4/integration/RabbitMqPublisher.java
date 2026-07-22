package br.com.postech.techchallenge.fase4.integration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import br.com.postech.techchallenge.fase4.model.Avaliacao;

public class RabbitMqPublisher {

    private static final String RABBITMQ_HOST = System.getenv("RABBITMQ_HOST") != null 
        ? System.getenv("RABBITMQ_HOST") 
        : "localhost";
    
    private static final int RABBITMQ_PORT = Integer.parseInt(
        System.getenv("RABBITMQ_PORT") != null ? System.getenv("RABBITMQ_PORT") : "5672"
    );
    
    private static final String RABBITMQ_USER = System.getenv("RABBITMQ_USER") != null 
        ? System.getenv("RABBITMQ_USER") 
        : "guest";
    
    private static final String RABBITMQ_PASSWORD = System.getenv("RABBITMQ_PASSWORD") != null 
        ? System.getenv("RABBITMQ_PASSWORD") 
        : "guest";
    
    private static final String QUEUE_NAME = "avaliacao.queue";
    private static final String EXCHANGE_NAME = "avaliacao.exchange";
    private static final String ROUTING_KEY = "avaliacao.routing.key";

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Publica uma avaliação na fila RabbitMQ
     * @param avaliacao Avaliação a ser publicada
     * @throws IOException Erro ao conectar ou publicar
     * @throws TimeoutException Timeout na conexão
     */
    public void publicarAvaliacao(Avaliacao avaliacao) throws IOException, TimeoutException {
        
        Connection connection = null;
        Channel channel = null;
        
        try {
            // Configurar factory de conexão
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBITMQ_HOST);
            factory.setPort(RABBITMQ_PORT);
            factory.setUsername(RABBITMQ_USER);
            factory.setPassword(RABBITMQ_PASSWORD);
            
            System.out.println("Conectando ao RabbitMQ em " + RABBITMQ_HOST + ":" + RABBITMQ_PORT);
            
            // Criar conexão e canal
            connection = factory.newConnection();
            channel = connection.createChannel();
            
            // Declarar exchange e fila (idempotente)
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            
            // Serializar avaliação para JSON
            String message = mapper.writeValueAsString(avaliacao);
            
            // Publicar mensagem
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
            
            System.out.println("Avaliação publicada com sucesso!");
            System.out.println("   ID: " + avaliacao.getId());
            System.out.println("   Nota: " + avaliacao.getNota());
            System.out.println("   Urgência: " + avaliacao.getUrgencia());
            
        } catch (IOException | TimeoutException e) {
            System.err.println("Erro ao publicar avaliação: " + e.getMessage());
            throw e;
        } finally {
            // Fechar recursos
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
    }
    
    /**
     * Verifica a conexão com RabbitMQ
     * @return true se conectado com sucesso
     */
    public boolean verificarConexao() {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBITMQ_HOST);
            factory.setPort(RABBITMQ_PORT);
            factory.setUsername(RABBITMQ_USER);
            factory.setPassword(RABBITMQ_PASSWORD);
            
            connection = factory.newConnection();
            System.out.println("RabbitMQ está acessível");
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao conectar RabbitMQ: " + e.getMessage());
            return false;
        } finally {
            if (connection != null && connection.isOpen()) {
                try {
                    connection.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
