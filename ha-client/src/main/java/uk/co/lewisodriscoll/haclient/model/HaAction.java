package uk.co.lewisodriscoll.haclient.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.IOException;

@Data
@JsonDeserialize(builder = HaAction.HaActionBuilder.class)
@Builder(builderClassName = "HaActionBuilder", toBuilder = true)
public class HaAction {

    @NonNull
    private String service;

    @NonNull
    private String action;

    private float value;

    public static HaAction fromJson(String json) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, HaAction.class);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class HaActionBuilder { }
}
