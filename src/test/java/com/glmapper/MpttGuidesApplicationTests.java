package com.glmapper;

import com.glmapper.entities.TestTreeNodeEntity;
import com.glmapper.mptt.TreeUtils;
import com.glmapper.repo.TestTreeNodeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MpttGuidesApplication.class)
public class MpttGuidesApplicationTests {

    @Autowired
    private TestTreeNodeRepository testTreeNodeRepository;


    @Test
    public void buildTree() throws Exception {
        TestTreeNodeEntity existedRoot = this.testTreeNodeRepository.findByName("root");
        if (existedRoot != null) {
            return;
        }
        TestTreeNodeEntity root = new TestTreeNodeEntity("root");
        Map<Integer, List<TestTreeNodeEntity>> tiers = new HashMap<>();
        List<TestTreeNodeEntity> roots = new ArrayList<>();
        roots.add(root);
        this.testTreeNodeRepository.setEntityClass(TestTreeNodeEntity.class);
        this.testTreeNodeRepository.startTree(root);
        tiers.put(0, roots);
        // 11 为 层数，下面方式构建出来的节点越 6W+ ，可以通过调整每层的个数来减少节点数量
        for (int i = 1; i <= 11; i++) {
            // 先把上一层的所有节点拿出来
            List<TestTreeNodeEntity> preParentTiers = tiers.get(i - 1);
            List<TestTreeNodeEntity> currentNodes = new ArrayList<>();
            int tierNodeCount = i <= 7 ? 3 : 2;
            for (int b = 0; b < preParentTiers.size(); b++) {
                TestTreeNodeEntity preParent = preParentTiers.get(b);
                for (int a = 1; a <= tierNodeCount; a++) {
                    TestTreeNodeEntity item = new TestTreeNodeEntity(buildName(preParent, i, a));
                    this.testTreeNodeRepository.addChild(preParent, item);
                    currentNodes.add(item);
                }
            }
            tiers.put(i, currentNodes);
        }
        System.out.println(tiers.size());
    }

    @Test
    public void test_find() throws Exception {
        this.testTreeNodeRepository.setEntityClass(TestTreeNodeEntity.class);
        TreeUtils utils = new TreeUtils<>(this.testTreeNodeRepository);
        // 查找节点
        TestTreeNodeEntity findChild = this.testTreeNodeRepository.findByName("node-1-1-1-1-1-1-1-1-1-1-2-1");
        // 查找父节点
        List<TestTreeNodeEntity> parent = this.testTreeNodeRepository.findAncestors(findChild);
        if (!parent.isEmpty()) {
            for (TestTreeNodeEntity item : parent) {
                System.out.println(utils.printRootNode(item));
            }
        }
        // 查找子树
        findChild = this.testTreeNodeRepository.findByName("node-1-2");
        List<TestTreeNodeEntity> subTree = this.testTreeNodeRepository.findSubTree(findChild);
        System.out.println(subTree.size());
    }


    static String buildName(TestTreeNodeEntity preParent, int i, int a) {
        System.out.println("i= " + i + "; parent= " + preParent.getName());
        if (i == 1) {
            return "node-" + i + "-" + a;
        } else {
            return preParent.getName() + "-" + a;
        }
    }

    @Test
    public void create() throws Exception {
        this.testTreeNodeRepository.setEntityClass(TestTreeNodeEntity.class);
        TestTreeNodeEntity root = new TestTreeNodeEntity("root");
        Long treeId = this.testTreeNodeRepository.startTree(root);

        TestTreeNodeEntity child1 = new TestTreeNodeEntity("child-1");
        this.testTreeNodeRepository.addChild(root, child1);

        TestTreeNodeEntity subChild1 = new TestTreeNodeEntity("subChild-1");
        this.testTreeNodeRepository.addChild(child1, subChild1);

        TestTreeNodeEntity subSubChild = new TestTreeNodeEntity("subSubChild");
        this.testTreeNodeRepository.addChild(subChild1, subSubChild);

        TestTreeNodeEntity subChild2 = new TestTreeNodeEntity("subChild-2");
        this.testTreeNodeRepository.addChild(child1, subChild2);

        TestTreeNodeEntity child2 = new TestTreeNodeEntity("child-2");
        this.testTreeNodeRepository.addChild(root, child2);

        TestTreeNodeEntity lastSubChild = new TestTreeNodeEntity("lastSubChild");
        this.testTreeNodeRepository.addChild(child2, lastSubChild);

        // 打印树
        TreeUtils utils = new TreeUtils<>(testTreeNodeRepository);
        TestTreeNodeEntity findRoot = testTreeNodeRepository.findTreeRoot(treeId);
        String fullTree = utils.printTree(findRoot);
        System.out.println(fullTree);

        // 打印子树
        TestTreeNodeEntity findChild1 = testTreeNodeRepository.findByName("child-1");
        String partialTree = utils.printTree(findChild1);
        System.out.println(partialTree);

        // 获取节点的直接子节点的排序列表
        TestTreeNodeEntity sortRoot = testTreeNodeRepository.findTreeRoot(treeId);
        List<TestTreeNodeEntity> directChildren = testTreeNodeRepository.findChildren(sortRoot);

        // 获取节点的祖先列表
        TestTreeNodeEntity findSubSubChild = testTreeNodeRepository.findByName("subSubChild");
        List<TestTreeNodeEntity> ancestors = testTreeNodeRepository.findAncestors(findSubSubChild);

        // 从父对象中删除子对象
        TestTreeNodeEntity root1 = testTreeNodeRepository.findByName("root");
        TestTreeNodeEntity byName = testTreeNodeRepository.findByName("child-1");
        List<TestTreeNodeEntity> testTreeNodeEntities = testTreeNodeRepository.removeChild(root1, byName);
    }
}
