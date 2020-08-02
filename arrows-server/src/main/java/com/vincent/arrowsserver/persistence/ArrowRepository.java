package com.vincent.arrowsserver.persistence;
import com.vincent.arrowsserver.model.Arrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArrowRepository extends JpaRepository<Arrow, Long> {

}
