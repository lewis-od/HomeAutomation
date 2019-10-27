package uk.co.lewisodriscoll.haclient.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class HaResponse {
    public enum Status {
        SUCCESS,
        ERROR;
    }

    private Status status;
    private String message;
}
