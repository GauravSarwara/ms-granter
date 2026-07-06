package com.granter.integrate.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    private String redirectUrl;
    private String webhookUrl;
    private String state;
}