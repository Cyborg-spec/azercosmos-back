package com.azercosmos.back.repository;

import com.azercosmos.back.entity.Leak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeakRepository extends JpaRepository<Leak, String> {
}
