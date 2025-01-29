package com.cs.doceho.stats.bot.v2.db.repository;

import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TopRepository extends JpaRepository<TopItem, Long>{

}
