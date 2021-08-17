package realestate.service;

import org.springframework.stereotype.Service;
import realestate.domain.Building;
import realestate.domain.Owner;
import realestate.dto.building.BuildingRequestDto;
import realestate.dto.building.BuildingResponseDto;
import realestate.dto.building.BuildingsResponseDto;
import realestate.dto.building.OwnerDto;
import realestate.dto.building.UpdateBuildingRequestDto;
import realestate.exception.ApplicationError;
import realestate.exception.RequestException;
import realestate.exception.ResourceNotFoundExceptionException;
import realestate.repository.BuildingRepository;
import realestate.repository.OwnerRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final OwnerRepository ownerRepository;

    public BuildingService(BuildingRepository buildingRepository, OwnerRepository ownerRepository) {
        this.buildingRepository = buildingRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public BuildingResponseDto createBuilding(BuildingRequestDto buildingRequestDto) throws RequestException {
        Owner owner = ownerRepository.findById(buildingRequestDto.getOwner().getId()).orElse(null);
        if (owner != null) {
            Building building = new Building();
            building.setBuildingId(UUID.randomUUID().toString());
            building.setAddress(buildingRequestDto.getAddress());
            building.setMarketValue(buildingRequestDto.getMarketValue());
            building.setPropertyType(buildingRequestDto.getPropertyType());
            building.setSize(buildingRequestDto.getSize());
            owner.setOwnerName(buildingRequestDto.getOwner().getOwnerName());
            owner.setId(buildingRequestDto.getOwner().getId());
            building.setOwner(owner);
            buildingRepository.save(building);
            return toBuildingResponseDto(building);

        } else throw new RequestException(ApplicationError.BAD_REQUEST);

    }

    private BuildingResponseDto toBuildingResponseDto(Building building) {
        BuildingResponseDto buildingResponseDto = new BuildingResponseDto();
        buildingResponseDto.setBuildingId(building.getBuildingId());
        buildingResponseDto.setAddress(building.getAddress());
        buildingResponseDto.setSize(building.getSize());
        buildingResponseDto.setMarketValue(building.getMarketValue());
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setOwnerName(building.getOwner().getOwnerName());
        ownerDto.setId(building.getOwner().getId());
        buildingResponseDto.setOwner(ownerDto);
        buildingResponseDto.setPropertyType(building.getPropertyType());

        return buildingResponseDto;
    }

    private BuildingsResponseDto toBuildingsResponseDto(List<Building> buildings) {
        BuildingsResponseDto buildingsResponseDto = new BuildingsResponseDto();
        buildingsResponseDto.setBuildings(buildings.stream().map(this::toBuildingResponseDto).collect(Collectors.toList()));
        return buildingsResponseDto;
    }

    public BuildingResponseDto getBuilding(String buildingId) throws ResourceNotFoundExceptionException {
        Building building = buildingRepository.findByBuildingId(buildingId).orElseThrow(ResourceNotFoundExceptionException::new);
        return toBuildingResponseDto(building);
    }

    public BuildingsResponseDto getBuildings(Long ownerId) throws ResourceNotFoundExceptionException {
        List<Building> buildings = buildingRepository.findAllByOwnerId(ownerId);
        return toBuildingsResponseDto(buildings);
    }

    @Transactional
    public BuildingResponseDto updateBuilding(String buildingId, UpdateBuildingRequestDto updateRequest)
            throws RequestException {
        Building building = buildingRepository.findByBuildingId(buildingId).orElseThrow(ResourceNotFoundExceptionException::new);

        if (updateRequest.getAddress() != null) {
            building.setAddress(updateRequest.getAddress());
        }

        if (updateRequest.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(updateRequest.getOwnerId()).orElseThrow(ResourceNotFoundExceptionException::new);
            if (owner != null) {
                building.setOwner(owner);
            }
        }
        if (updateRequest.getMarketValue() != null) {
            building.setMarketValue(updateRequest.getMarketValue());
        }
        if (updateRequest.getPropertyType() != null) {
            building.setPropertyType(updateRequest.getPropertyType());
        }
        if (updateRequest.getSize() != null) {
            building.setSize(updateRequest.getSize());
        }

        buildingRepository.save(building);

        return toBuildingResponseDto(building);
    }

}
