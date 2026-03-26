package com.granter.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API Response Wrapper
 * @param <T> type of response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponsePojo<T> {

    // SUCCESS / FAILURE
    private String status;

    // Human-readable message
    private String message;

    // Actual response data
    private T data;

  
    /**
     * Success Response (with data)
     */
    public static <T> GenericResponsePojo<T> success(T data, String message) {
        return GenericResponsePojo.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)                
                .build();
    }

    /**
     * Success Response (without data)
     */
    public static <T> GenericResponsePojo<T> success(String message) {
        return GenericResponsePojo.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(null)
                .build();
    }

    /**
     * Failure Response
     */
    public static <T> GenericResponsePojo<T> failure(String message, Object error) {
        return GenericResponsePojo.<T>builder()
                .status("FAILURE")
                .message(message)
                .data(null)
                .build();
    }
}