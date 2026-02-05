package com.finrating.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendDatasetProcessingMessage(Long datasetId) {
        kafkaTemplate.send("dataset-processing", datasetId.toString());
    }
}
