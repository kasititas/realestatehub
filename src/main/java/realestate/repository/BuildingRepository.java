package realestate.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import realestate.domain.Building;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends CrudRepository<Building, Long> {

    Optional<Building> findByBuildingId(String buildingId);

    @Query("select b from Building b where b.owner.id = :ownerId")
    List<Building> findAllByOwnerId(Long ownerId);

    @Modifying
    @Transactional
    void deleteByBuildingId(String paymentId);

}
