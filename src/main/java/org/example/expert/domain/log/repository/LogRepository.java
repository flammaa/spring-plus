package org.example.expert.domain.log.repository;

import org.example.expert.domain.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Lv3-11
@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
