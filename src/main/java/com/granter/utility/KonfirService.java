package com.granter.utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.granter.integrate.request.KonfirRequest;
import com.granter.integrate.response.KonfirResponse;

@Service
@RequiredArgsConstructor
public class KonfirService {

    private final RestTemplate restTemplate;

    @Value("${konfir.url}")
    private String url;

    @Value("${konfir.client-id}")
    private String clientId;

    @Value("${konfir.client-secret}")
    private String clientSecret;

    public KonfirResponse createVerification(KonfirRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-KONFIR-CLIENT-ID", clientId);
        headers.set("X-KONFIR-CLIENT-SECRET", clientSecret);

        HttpEntity<KonfirRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<KonfirResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        KonfirResponse.class);

        return response.getBody();
    }
}