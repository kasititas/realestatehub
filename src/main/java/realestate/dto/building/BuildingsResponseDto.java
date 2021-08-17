package realestate.dto.building;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingsResponseDto {
    List<BuildingResponseDto> buildings;

    public BuildingsResponseDto() {
        this.buildings = new ArrayList<>();
    }

    public List<BuildingResponseDto> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingResponseDto> buildings) {
        this.buildings = buildings;
    }
}
