package uk.co.lewisodriscoll.haclient.model;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class HaResponse {
    public enum Status {
        SUCCESS,
        ERROR;
    }

    private final Status status;
    private final String message;
}
