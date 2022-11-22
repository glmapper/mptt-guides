package com.glmapper.repo;

import com.glmapper.entities.TestTreeNodeEntity;
import com.glmapper.mptt.DyadicRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * 自定义扩展的 repository 实现类
 */
@Repository(value = "testTreeNodeRepositoryCustomImpl")
public class TestTreeNodeRepositoryCustomImpl extends DyadicRepositoryImpl<TestTreeNodeEntity> implements TestTreeNodeRepositoryCustom {
}
