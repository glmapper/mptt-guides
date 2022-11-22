package com.glmapper.mptt;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

public interface TreeRepository<T extends TreeEntity> {

    void setEntityClass(Class<T> entityClass);

    T createNode(String name) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;

    Long startTree(T node) throws TreeRepository.NodeAlreadyAttachedToTree;

    T findTreeRoot(Long treeId) throws NoResultException;

    void addChild(T parent, T child) throws TreeRepository.NodeNotInTree, TreeRepository.NodeAlreadyAttachedToTree;

    List<T> removeChild(T parent, T child) throws TreeRepository.NodeNotInTree, TreeRepository.NodeNotChildOfParent;

    List<T> findChildren(T node);

    List<T> findSubTree(T node);

    List<T> findAncestors(T node);

    Optional<T> findParent(T node);

    public static class NodeNotChildOfParent extends Exception {
        public NodeNotChildOfParent(String message) {
            super(message);
        }
    }

    public static class NodeNotInTree extends Exception {
        public NodeNotInTree(String message) {
            super(message);
        }
    }

    public static class NodeAlreadyAttachedToTree extends Exception {
        public NodeAlreadyAttachedToTree(String message) {
            super(message);
        }
    }
}

