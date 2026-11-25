package com.dailyit.dlrm.core.repository;

import com.dailyit.dlrm.core.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByIdAsc();
}
