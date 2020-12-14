package org.launchcode.brewpub.models.data;

import org.launchcode.brewpub.models.Pub;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PubRepository extends CrudRepository<Pub, Integer> {
}
