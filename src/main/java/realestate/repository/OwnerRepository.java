package realestate.repository;

import org.springframework.data.repository.CrudRepository;
import realestate.domain.Owner;

import java.util.Optional;

public interface OwnerRepository extends CrudRepository<Owner, Long> {
    @Override
    Optional<Owner> findById(Long id);
}
