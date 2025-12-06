package com.azercosmos.back.repository;

import com.azercosmos.back.entity.LeakCoordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeakCoordinateRepository extends JpaRepository<LeakCoordinate, Long> {
}
