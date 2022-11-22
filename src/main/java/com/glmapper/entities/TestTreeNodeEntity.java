package com.glmapper.entities;

import com.glmapper.mptt.DyadicEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_tree_data")
public class TestTreeNodeEntity extends DyadicEntity {

    @SuppressWarnings({"Unused"})
    public TestTreeNodeEntity() {
        super();
    }

    public TestTreeNodeEntity(String name) {
        super(name);
    }
}
