package db.repository;

import db.model.TopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TopRepository extends JpaRepository<TopItem, Long>{

}
