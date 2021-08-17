package realestate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import realestate.domain.Building;
import realestate.repository.BuildingRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Service
public class TaxService {

    private final BuildingRepository buildingRepository;

    private Properties taxRates;

    @Value("classpath:/tax-rates.properties")
    private Resource taxResource;

    @Autowired
    private void handleProperties() throws IOException {
        if (taxRates == null) {
            taxRates = new Properties();
            try (Reader reader = new InputStreamReader(taxResource.getInputStream(), StandardCharsets.UTF_8)) {
                taxRates.load(reader);
            }
        }
    }

    public TaxService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public String calculateTotalYearlyRealEstateTax(Long ownerId) {

        List<Building> buildings = buildingRepository.findAllByOwnerId(ownerId);
        BigDecimal totalYearlyRealEstateTax = BigDecimal.ZERO;
        for (Building building : buildings) {
            BigDecimal taxrate = new BigDecimal(taxRates.getProperty(
                    building.getPropertyType().toString().toLowerCase() + ".tax.rate"));
            BigDecimal marketValue = new BigDecimal(building.getMarketValue());
            totalYearlyRealEstateTax = totalYearlyRealEstateTax.add(marketValue.multiply(taxrate));
            totalYearlyRealEstateTax = totalYearlyRealEstateTax.setScale(2, RoundingMode.HALF_UP);

        }
        return totalYearlyRealEstateTax.toString();
    }

    public Properties getTaxRates() {
        return taxRates;
    }
}
