package com.granter.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationCompletedWebhook {

    private String event;
    private DataObject data;
    private Object meta;
}