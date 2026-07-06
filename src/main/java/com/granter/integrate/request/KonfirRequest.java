package com.granter.integrate.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KonfirRequest {
    private DataPayload data;
    private Meta meta;
}