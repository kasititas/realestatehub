package realestate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import realestate.dto.building.BuildingRequestDto;
import realestate.dto.building.BuildingResponseDto;
import realestate.dto.building.BuildingsResponseDto;
import realestate.dto.building.UpdateBuildingRequestDto;
import realestate.exception.RequestException;
import realestate.service.BuildingService;
import realestate.service.TaxService;

import javax.validation.Valid;

@RestController
public class BuildingController {

    private final BuildingService buildingService;
    private final TaxService taxService;

    public BuildingController(BuildingService buildingService, TaxService taxService) {
        this.buildingService = buildingService;
        this.taxService = taxService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/v1/buildings")
    public BuildingResponseDto createBuilding(@Valid @RequestBody BuildingRequestDto buildingRequestDto) throws RequestException {
        return buildingService.createBuilding(buildingRequestDto);
    }

    @GetMapping(value = "/v1/buildings/{buildingId}")
    public BuildingResponseDto getBuilding(@PathVariable String buildingId)
            throws RequestException {
        return buildingService.getBuilding(buildingId);
    }

    @GetMapping(value = "/v1/buildings")
    public BuildingsResponseDto getBuilding(@RequestParam Long ownerId)
            throws RequestException {
        return buildingService.getBuildings(ownerId);
    }

    @PatchMapping(value = "/v1/buildings/{buildingId}")
    public BuildingResponseDto updateBuilding(@PathVariable String buildingId,
                                              @Valid @RequestBody UpdateBuildingRequestDto updateBuildingRequestDto)
            throws RequestException {
        return buildingService.updateBuilding(buildingId, updateBuildingRequestDto);
    }

    @GetMapping(value = "/v1/buildings/taxes")
    public String getBuildingsTaxes(@RequestParam Long ownerId)
            throws RequestException {
        return taxService.calculateTotalYearlyRealEstateTax(ownerId);
    }

}
