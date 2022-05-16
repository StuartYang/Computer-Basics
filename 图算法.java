package com.yxd.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

class GraphNode {
    int value; // 值

    int in; // 一个点的入度 一个线指向自己的就算入度

    int out; // 一个点的出度

    ArrayList<GraphNode> nexts; // 从当前点发散出去的边直接指向的点有哪些？ == in的数量

    ArrayList<GraphEdge> edges; // 当前点发散出去的边有哪些？自己拥有的边

    public GraphNode(int value) {
        this.value = value;
        this.in = 0;
        this.out = 0;
        this.nexts = new ArrayList<>();
        this.edges = new ArrayList<>();
    }
}

class GraphEdge {
    int weight;

    GraphNode from; // 从哪里发出的

    GraphNode to; // 到哪里的

    public GraphEdge(int weight, GraphNode from, GraphNode to) {
        this.weight = weight;
        this.from = from;
        this.to = to;
    }
}

/**
 * 拉链法
 */
public class Graph {

    HashMap<Integer, GraphNode> nodes;

    HashSet<GraphEdge> edges;

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new HashSet<>();
    }

    /**
     * 构建自己熟悉的图数据结构
     *
     * @param matrix
     * @return
     */
    Graph createGraph(Integer[][] matrix) {
        Graph graph = new Graph();

        for (int i = 0; i < matrix.length; i++) {

            Integer from = matrix[i][0];
            Integer to = matrix[i][1];
            Integer weight = matrix[i][2];

            // 将点添加到hashMap中
            if (!graph.nodes.containsKey(from)) {
                graph.nodes.put(to, new GraphNode(from));
            }
            if (!graph.nodes.containsKey(to)) {
                graph.nodes.put(to, new GraphNode(to));
            }

            // 从nodes数据中获取该节点。不直接使用from是因为可能取到重复的
            GraphNode fromNode = graph.nodes.get(from);
            GraphNode toNode = graph.nodes.get(to);

            GraphEdge newEdge = new GraphEdge(weight, fromNode, toNode); //新的边
            fromNode.nexts.add(toNode); // from添加to点
            fromNode.out++;
            toNode.in++;
            fromNode.edges.add(newEdge); // from节点需要添加边集
            graph.edges.add(newEdge); // 边集中添加新边
        }

        return graph;
    }

    /**
     * 宽度优先遍历 / 广度优先遍历 ---->队列 bfs
     */

    void bfs(GraphNode node) {
        if (node == null) {
            return;
        }

        Queue queue = new LinkedList();
        HashSet set = new HashSet(); // 是为了配合无向图

        queue.add(node);

        set.add(node);
        // O(n^2)
        while (!queue.isEmpty()) {
            GraphNode cur = (GraphNode) queue.poll();
            System.out.println(cur.value); // 处理
            for (GraphNode next : cur.nexts) {
                if (!set.contains(next)) {
                    set.add(next);
                    queue.add(next);
                }
            }
        }
    }

    /**
     * 深度优先遍历 DFS
     */
    void dfs(GraphNode node) {
        if (node == null) {
            return;
        }

        Stack stack = new Stack();
        HashSet set = new HashSet();

        stack.push(node);
        set.add(node);
        System.out.println(node.value);

        // O(n^2)
        while (!stack.isEmpty()) {
            GraphNode cur = (GraphNode) stack.pop();
            for (GraphNode next : cur.nexts) {
                if (!set.contains(next)) {// set只放不存在的子节点
                    stack.push(cur); // 重新将父节点压回去
                    stack.push(next);// 再压子节点
                    set.add(next);
                    System.out.println(next.value); // 打印
                    break; // 不在看子孩子的兄弟节点
                }
            }
        }
    }

    /**
     * 拓扑排序（避免循环依赖，找到加载顺序）
     * : 图中不能有环
     */
    void topologySort(Graph graph) {

        // <GraphNode, Integer>： 节点，入度
        HashMap<GraphNode, Integer> inMap = new HashMap<>();
        // 入度为0的结点，才能进入队列
        Queue zeroQueue = new LinkedList();

        for (GraphNode node : graph.nodes.values()) {
            inMap.put(node, node.in);
            if (node.in == 0) {
                zeroQueue.add(node);
            }
        }

        List<GraphNode> result = new ArrayList<>();
        while (!zeroQueue.isEmpty()) {
            GraphNode cur = (GraphNode) zeroQueue.poll();
            result.add(cur);
            for (GraphNode next : cur.nexts) { // 遍历自己的的下一层孩子
                inMap.put(next, inMap.get(next) - 1); // 每个孩都放入hashMap中 ,将孩子节点的入度都-1（因为父节点已经被放入zeroQueue中了）
                if (inMap.get(next) == 0) { // 孩子节点入度为0的结点就添加到zeroQueue中
                    zeroQueue.add(next);
                }
            }
        }

    }

    /**
     * kruskal 无向图 最小生成树
     * {并查集}
     * 从点出发
     */
    HashMap<GraphNode, List<GraphNode>> setMap = new HashMap<>();

    void setKruskalMap(List<GraphNode> nodes) {

        for (GraphNode cur : nodes) {
            List<GraphNode> set = new ArrayList<>();
            set.add(cur);
            setMap.put(cur, set); // 一个链表放一个自己
        }
    }

    private boolean ifSameSet(GraphNode from, GraphNode to) {
        List<GraphNode> fromSet = setMap.get(from);
        List<GraphNode> toSet = setMap.get(to);
        return fromSet == toSet;
    }

    void union(GraphNode from, GraphNode to){
        List<GraphNode> fromSet = setMap.get(from);
        List<GraphNode> toSet = setMap.get(to);

        for (GraphNode toNode : toSet) {
            fromSet.add(toNode);
            setMap.put(toNode,fromSet);
        }


    }

    /**
     * prime 无向图 最小生成树
     */

    void prime() {

    }
}
