package realestate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import realestate.TestsBase;
import realestate.domain.Owner;
import realestate.dto.building.BuildingRequestDto;
import realestate.dto.building.BuildingResponseDto;
import realestate.dto.building.OwnerDto;
import realestate.dto.building.PropertyType;
import realestate.dto.building.UpdateBuildingRequestDto;
import realestate.exception.ApplicationError;
import realestate.exception.ErrorResponse;
import realestate.repository.BuildingRepository;
import realestate.repository.OwnerRepository;
import realestate.service.TaxService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class BuildingControllerTest extends TestsBase {

    private String buildingId;
    private BuildingRequestDto createRequest;
    private Owner owner;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private TaxService taxService;

    @BeforeEach
    public void setUp() {
        owner = new Owner();
        owner.setOwnerName("Name");
        owner = ownerRepository.save(owner);
    }

    @AfterAll
    public void tearDown() {
        Collections.singletonList(buildingId).forEach(id -> {
            if (id != null) {
                buildingRepository.deleteByBuildingId(id);
            }
        });
    }

    @Test
    public void testCreateBuilding() throws Exception {
        createRequest = getBuildingRequestDto();
        MvcResult result = getMockMvc().perform(buildPostBuilding(createRequest))
                .andExpect(status().isCreated())
                .andReturn();
        BuildingResponseDto buildingResponseDto = jsonObjectMapper.readValue(
                result.getResponse().getContentAsString(), BuildingResponseDto.class);

        buildingId = buildingResponseDto.getBuildingId();
    }

    @Test
    public void testGetbuildingNotFound() throws Exception {
        MvcResult result = getMockMvc()
                .perform(buildGetBuilding("not_existing_id"))
                .andExpect(status().isNotFound())
                .andReturn();
        ErrorResponse response = jsonObjectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        Assertions.assertEquals(ApplicationError.NOT_FOUND.getCode(), response.getCode());
        Assertions.assertEquals(ApplicationError.NOT_FOUND.getDefaultMessage(), response.getMessage());
    }

    @Nested
    class AfterBuildingCreate {
        @Test
        public void testGetBuilding() throws Exception {
            MvcResult result = getMockMvc()
                    .perform(buildGetBuilding(buildingId))
                    .andExpect(status().isOk())
                    .andReturn();
            BuildingResponseDto responseDto = jsonObjectMapper.readValue(
                    result.getResponse().getContentAsString(), BuildingResponseDto.class
            );

            Assertions.assertEquals(buildingId, responseDto.getBuildingId());
            Assertions.assertEquals(createRequest.getOwner().getId(), responseDto.getOwner().getId());
            Assertions.assertEquals(createRequest.getAddress(), responseDto.getAddress());
            Assertions.assertEquals(createRequest.getMarketValue(), responseDto.getMarketValue());
            Assertions.assertEquals(createRequest.getSize(), responseDto.getSize());
            Assertions.assertEquals(createRequest.getPropertyType(), responseDto.getPropertyType());

        }

        @Nested
        class AfterBuildingGetTryUpdate {
            @Test
            public void testUpdateBuilding() throws Exception {

                UpdateBuildingRequestDto requestDto = new UpdateBuildingRequestDto();
                requestDto.setAddress("new City, new City street, new number");
                requestDto.setMarketValue("300000");
                requestDto.setOwnerId(owner.getId());
                MvcResult result = getMockMvc()
                        .perform(buildPatchBuilding(buildingId, requestDto))
                        .andExpect(status().isOk())
                        .andReturn();

                BuildingResponseDto responseDto = jsonObjectMapper.readValue(
                        result.getResponse().getContentAsString(), BuildingResponseDto.class
                );

                Assertions.assertEquals(buildingId, responseDto.getBuildingId());
                Assertions.assertEquals(requestDto.getOwnerId(), responseDto.getOwner().getId());
                Assertions.assertEquals(requestDto.getAddress(), responseDto.getAddress());
                Assertions.assertEquals(requestDto.getMarketValue(), responseDto.getMarketValue());

            }

        }

        @Nested
        class AfterUpdateBuilding {

            private BigDecimal expectedtotalYearlyRealEstateTax;

            @Test
            public void testGetBuildingsTaxes() throws Exception {
                BigDecimal sum = BigDecimal.ZERO;
                for (int i = 0; i < 3; i++) {
                    createRequest = getBuildingRequestDto();
                    Random r = new Random();
                    int low = 100000;
                    int high = 300000;
                    int result = r.nextInt(high - low) + low;
                    createRequest.setMarketValue(String.valueOf(result));
                    createRequest.setAddress(createRequest.getAddress() + i);

                    getMockMvc().perform(buildPostBuilding(createRequest))
                            .andExpect(status().isCreated())
                            .andReturn();
                     sum = sum.add(new BigDecimal(result));
                }
                Properties taxes = taxService.getTaxRates();
                expectedtotalYearlyRealEstateTax = sum.multiply(new BigDecimal(taxes.getProperty(createRequest.getPropertyType().toString().toLowerCase() + ".tax.rate")));
                expectedtotalYearlyRealEstateTax = expectedtotalYearlyRealEstateTax.setScale(2, RoundingMode.HALF_UP);
                MvcResult result = getMockMvc()
                        .perform(buildGetBuildingsTaxes(createRequest.getOwner().getId()))
                        .andExpect(status().isOk())
                        .andReturn();
                String totalYearlyRealEstateTax = jsonObjectMapper.readValue(
                        result.getResponse().getContentAsString(), String.class);

                Assertions.assertEquals(expectedtotalYearlyRealEstateTax.toString(), totalYearlyRealEstateTax);

            }
        }

    }


    private BuildingRequestDto getBuildingRequestDto() {
        BuildingRequestDto buildingRequestDto = new BuildingRequestDto();
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(owner.getId());
        ownerDto.setOwnerName(owner.getOwnerName());
        buildingRequestDto.setOwner(ownerDto);
        buildingRequestDto.setAddress("City, City street, 8");
        buildingRequestDto.setMarketValue("200000");
        buildingRequestDto.setSize("400");
        buildingRequestDto.setPropertyType(PropertyType.APARTMENT);
        return buildingRequestDto;
    }

    private MockHttpServletRequestBuilder buildPostBuilding(BuildingRequestDto requestDto) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/v1/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(requestDto));
    }

    private MockHttpServletRequestBuilder buildGetBuilding(String buildingId) {
        return MockMvcRequestBuilders.get("/v1/buildings/{buildingId}", buildingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder buildPatchBuilding(String buildingId, UpdateBuildingRequestDto requestDto) throws JsonProcessingException {
        return MockMvcRequestBuilders.patch("/v1/buildings/{buildingId}", buildingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(requestDto));
    }

    private MockHttpServletRequestBuilder buildGetBuildingsTaxes(Long ownerId) {
        return MockMvcRequestBuilders.get("/v1/buildings/taxes")
                .param("ownerId", String.valueOf(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }
}
