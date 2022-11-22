package com.glmapper.mptt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TreeUtils<T extends TreeEntity> {
  private TreeRepository<T> treeRepo;

  public TreeUtils(TreeRepository treeRepo) {
    this.treeRepo = treeRepo;
  }

  /**
   * Prints a string representation of the tree / sub-tree of a given node.
   * <p>
   * Very useful for tests when comparing whole trees.
   *
   * @param node must not be null; must be part of a tree
   * @return the string representation of the tree / sub-tree
   */
  public String printTree(T node) {
    String rootString = printRootNode(node);

    List<T> children = treeRepo.findChildren(node);
    List<Integer> levels = Collections.unmodifiableList(Collections.singletonList(0));
    String childrenString = children.isEmpty() ? "" :
        "\n" +
            IntStream.range(0, children.size())
                .mapToObj(i -> i < children.size() - 1 ?
                    printSubTree(children.get(i), levels, false) :
                    printSubTree(children.get(i), levels, true))
                .collect(Collectors.joining("\n"));

    return rootString + childrenString;
  }

  protected String printSubTree(T node, List<Integer> levels, boolean isLast) {
    List<T> children = treeRepo.findChildren(node);
    List<Integer> nextLevels = concatLevel(levels, isLast ? 0 : 1);
    return
        (isLast ? printLastChildNode(node, levels) : printChildNode(node, levels)) +
            (children.isEmpty() ? "" : "\n" +
                IntStream.range(0, children.size())
                    .mapToObj(i -> i < children.size() - 1 ?
                        printSubTree(children.get(i), nextLevels, false) :
                        printSubTree(children.get(i), nextLevels, true))
                    .collect(Collectors.joining("\n")));
  }

  protected List<Integer> concatLevel(List<Integer> levels, Integer level) {
    return Stream.concat(levels.stream(), Stream.of(level))
        .collect(Collectors.toList());
  }

  public String printRootNode(T node) {
    return String.format(
        // @formatter:off
        ".\n" +
        "└── %s",
        // @formatter:on
        node.toString());
  }

  protected String printChildNode(T node, List<Integer> levels) {
    return String.format("%s├── %s",
        printLevelPrefix(levels),
        node.toString());
  }

  protected String printLastChildNode(T node, List<Integer> levels) {
    return String.format("%s└── %s",
        printLevelPrefix(levels),
        node.toString());
  }

  protected String printLevelPrefix(List<Integer> levels) {
    return levels.stream()
        .map(i -> i == 0 ? "    " : "│   ")
        .collect(Collectors.joining());
  }
}
