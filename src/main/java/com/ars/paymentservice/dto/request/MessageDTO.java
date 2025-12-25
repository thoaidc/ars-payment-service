package com.ars.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public class MessageDTO {
    @NotBlank
    private String topic;
    @NotBlank
    private String content;
    private String clientId;

    public MessageDTO(String topic, String content, String clientId) {
        this.topic = topic;
        this.content = content;
        this.clientId = clientId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String topic;
        private String content;
        private String clientId;

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public MessageDTO build() {
            return new MessageDTO(topic, content, clientId);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "topic=" + topic + ", clientId=" + clientId;
    }
}
