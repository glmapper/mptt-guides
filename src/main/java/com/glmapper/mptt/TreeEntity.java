package com.glmapper.mptt;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class TreeEntity<T extends Number> {
    private static final String NO_NAME = "NO_NAME";
    public static final long NO_TREE_ID = -1L;
    public static final long START = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    protected long treeId;

    @Column(nullable = false)
    private long depth;

    @Column(nullable = false)
    protected T lft;

    @Column(nullable = false)
    protected T rgt;

    public TreeEntity() {
        this.name = "NO_NAME";
        this.setDefaults();
    }

    public TreeEntity(String name) {
        this.name = name;
        this.setDefaults();
    }

    public void setDefaults() {
        this.treeId = -1L;
        this.depth = 0L;
        this.lft = this.getStartLft();
        this.rgt = this.getStartRgt();
    }

    public abstract T getStartLft();

    public abstract T getStartRgt();

    public long getId() {
        return this.id;
    }

    public boolean hasTreeId() {
        return this.treeId != -1L;
    }

    public long getTreeId() {
        return this.treeId;
    }

    public void setTreeId(long treeId) {
        this.treeId = treeId;
    }

    public long getDepth() {
        return this.depth;
    }

    public void setDepth(long depth) {
        this.depth = depth;
    }

    public T getLft() {
        return this.lft;
    }

    public void setLft(T lft) {
        this.lft = lft;
    }

    public T getRgt() {
        return this.rgt;
    }

    public void setRgt(T rgt) {
        this.rgt = rgt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected String toNodeString() {
        return String.format("[treeId: %s | lft: %s | rgt: %s]", this.getTreeId(), this.getLft(), this.getRgt());
    }

    public String toString() {
        return String.format("%s (id: %d) %s", this.getName(), this.getId(), this.toNodeString());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.toString()});
    }

    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }
}

