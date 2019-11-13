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

    private Status status;
    private String message;
}
