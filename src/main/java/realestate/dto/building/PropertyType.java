package realestate.dto.building;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PropertyType {
    @JsonProperty("apartment")
    APARTMENT,
    @JsonProperty("house")
    HOUSE,
    @JsonProperty("industrial")
    INDUSTRIAL
}
