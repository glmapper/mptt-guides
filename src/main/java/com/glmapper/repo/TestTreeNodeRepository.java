package com.glmapper.repo;

import com.glmapper.entities.TestTreeNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTreeNodeRepository extends JpaRepository<TestTreeNodeEntity, Long>, TestTreeNodeRepositoryCustom {
    TestTreeNodeEntity findByName(String name);
}
