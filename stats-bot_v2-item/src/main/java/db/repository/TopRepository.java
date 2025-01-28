package db.repository;

import com.example.SpringDemoBot.model.Top;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TopRepository extends JpaRepository<Top, Long>{

}
