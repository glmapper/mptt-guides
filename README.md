## 分层结构数据管理介绍
Mysql 官方文档中对于分层数据的存储有一篇专门的文章来介绍：[MySQL :: Managing Hierarchical Data in MySQL](http://download.nust.na/pub6/mysql/tech-resources/articles/hierarchical-data.html)。这篇文档中首先阐述了分层数据的管理并不是关系数据库的目的，关系数据库的表不是分层的（如 XML），而只是一个平面列表；也就是说对于分层数据，使用关系型数据库来管理是不怎么合适的；出于对实际业务场景使用考虑，文章中给出了两种管理分层数据的方式，分别是** The Adjacency List Model 和 The Nested Set Model **。除 Mysql 官方文档中提到的两种方式之外，业界对于分层数据管理还有其他两种方式：

- [Managing hierarchical data in MySQL - closure table](https://www.waitingforcode.com/mysql/managing-hierarchical-data-in-mysql-closure-table/read)
- [Managing hierarchical data in MySQL - path enumeration](https://www.waitingforcode.com/mysql/managing-hierarchical-data-in-mysql-path-enumeration/read)

下表是各种管理方式的优缺点对比：

|  | 优点 | 缺点 |
| --- | --- | --- |
| 邻接表模型 | 实现简单，容易理解 | 检索复杂，需要深度嵌套；叶子节点修改时简单，对于中间节点修改复杂；有可能破坏记录的完整性 |
| 闭包表 | 易理解，查询子树简单 | 需要额外的关系表，插入和更新节点；移动节点也非常复杂，需要为正在移动的子树中的每个节点重新计算闭包表 |
| 路径枚举 | 实现简单，便于查找；插入节点简单 | 移动复杂，中间节点修改或者移动，所有子节点的path都得跟着修改；参照完整性缺失，不能确保父节点都正确存在（因为path只是结构化字符串的一部分） |
| 嵌套集 | 查找简单，查找性能好 | 层次结构重组（添加或删除节点）需要大约一半的树节点重新标记以维护嵌套集合模型 |

## MPTT + Nested Set & MPTT + **Nested Intervals**
> MPTT, or _modified preorder tree traversal_, is an efficient way to store hierarchical data in the flat structure of a relational database table. It uses the nested set model as it provides a faster option for read operations compared to the tree traversal operations of an adjacency list.


### MPTT + Nested Set(The Classic MPTT Structure)
嵌套集模式是 MPTT 经典结构的实现方式，它使用一种技术根据树遍历对节点进行编号，该遍历访问每个节点两次，并在两次访问时按访问顺序分配编号。这为每个节点留下了两个数字，它们存储为两个属性。

- 编号之前

![image.png](https://cdn.nlark.com/yuque/0/2022/png/230565/1669099681733-b4a284ed-3e00-4d7e-8ca8-d24091133a85.png#averageHue=%23fbfbfb&clientId=ud4e9e7ec-0d26-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=383&id=ftW0s&margin=%5Bobject%20Object%5D&name=image.png&originHeight=421&originWidth=771&originalType=binary&ratio=1&rotation=0&showTitle=false&size=17646&status=done&style=none&taskId=uc0b19ce0-b89e-4e8b-9c7b-ff246d138f9&title=&width=700.5)

- 编号之后

![image.png](https://cdn.nlark.com/yuque/0/2022/png/230565/1669099715589-f75eeb5a-22b9-4434-8e00-5f273156d16c.png#averageHue=%23fbf8f5&clientId=ud4e9e7ec-0d26-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=390&id=CPGLC&margin=%5Bobject%20Object%5D&name=image.png&originHeight=501&originWidth=883&originalType=binary&ratio=1&rotation=0&showTitle=false&size=55284&status=done&style=none&taskId=u98ffb766-5a7d-49d0-a01e-1539c32e84c&title=&width=686.5)

- 存储模型如下表
  | **ID** | **Name** | **TREE_ID** | **LFT** | **RGT** |
  | --- | --- | --- | --- | --- |
  | 1 | root | 100 | 1 | 14 |
  | 2 | child-1 | 100 | 2 | 9 |
  | 3 | subChild-1 | 100 | 3 | 6 |
  | 4 | subSubChild | 100 | 4 | 5 |
  | 5 | subChild-2 | 100 | 7 | 8 |
  | 6 | child-2 | 100 | 10 | 13 |
  | 7 | lastChild | 100 | 11 | 12 |


这种方式对于查询和读取操作非常有利，不需要通过递归方式查找树（子树），通过简单的 lft 和 rgt 的值比较就可以找到对应的结果集。但是层次结构重组（添加或删除节点）需要对大约一半的树节点进行重新标记，以便维持嵌套模型。
### MPTT + Nested **Intervals(**Dyadic Fractions as MPTT Structure**)**

在这篇文章 [https://github.com/hacker-works/mptt-jpa/tree/develop](https://github.com/hacker-works/mptt-jpa/tree/develop) 中提到了一种新的模型，即使用 Nested Intervals 代替了 Nested Set, 这里建立的树是以二叉树的形式建立，新节点的区间由父区间和最年轻的孩子决定。

> 嵌套间隔模型，是针对嵌套集合模型在层次结构重组时需要进行大量重新标记问题的优化。

- 结构图

![image.png](https://cdn.nlark.com/yuque/0/2022/png/230565/1669101400636-77eb02c4-ee3c-4a75-9390-3cbcc423f8e2.png#averageHue=%23fafaf9&clientId=ud4e9e7ec-0d26-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=585&id=ua616d702&margin=%5Bobject%20Object%5D&name=image.png&originHeight=705&originWidth=801&originalType=binary&ratio=1&rotation=0&showTitle=false&size=66894&status=done&style=none&taskId=u228faf45-3ec7-4018-9767-51221862bdb&title=&width=664.5)

- 表结构
  | **ID** | **Name** | **TREE_ID** | **LFT** | **RGT** |
  | --- | --- | --- | --- | --- |
  | 1 | root | 100 | 0/1 | 1/1 |
  | 2 | child-1 | 100 | 0/1 | 1/2 |
  | 3 | subChild-1 | 100 | 0/1 | 1/4 |
  | 4 | subSubChild | 100 | 0/1 | 1/8 |
  | 5 | subChild-2 | 100 | 1/4 | 3/8 |
  | 6 | child-2 | 100 | 1/2 | 3/4 |
  | 7 | lastChild | 100 | 1/2 | 5/8 |


### 测试
see code..

## 关于节点移动和节点更新
todo..

## 参考

- [https://www.baeldung.com/cs/storing-tree-in-rdb](https://www.baeldung.com/cs/storing-tree-in-rdb)
- [mysql 树形数据，层级数据Managing Hierarchical Data in MySQL - youxin - 博客园](https://www.cnblogs.com/youxin/p/3614726.html)
- [https://myusufirfanh.medium.com/storing-and-querying-hierarchical-data-in-sql-uplines-downlines-29cf4c725189](https://myusufirfanh.medium.com/storing-and-querying-hierarchical-data-in-sql-uplines-downlines-29cf4c725189)
- [https://sigmodrecord.org/publications/sigmodRecord/0506/p47-article-tropashko.pdf](https://sigmodrecord.org/publications/sigmodRecord/0506/p47-article-tropashko.pdf)
- [https://github.com/hacker-works/mptt-jpa/tree/develop](https://github.com/hacker-works/mptt-jpa/tree/develop)
