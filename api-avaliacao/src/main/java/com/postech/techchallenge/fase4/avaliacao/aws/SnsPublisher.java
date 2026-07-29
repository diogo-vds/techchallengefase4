package com.postech.techchallenge.fase4.avaliacao.aws;

import com.postech.techchallenge.fase4.avaliacao.dto.AvaliacaoEventoRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
public class SnsPublisher {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public SnsPublisher(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publicar(String mensagem) {

        PublishRequest request =
                PublishRequest.builder()
                        .topicArn(topicArn)
                        .message(mensagem)
                        .build();
        snsClient.publish(request);
    }

}
