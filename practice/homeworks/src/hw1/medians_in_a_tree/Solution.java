package hw1.medians_in_a_tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.hackerrank.com/contests/projector-algo-advanced-1-hw-1/challenges/medians-in-a-tree
 * Idea: LCA of two nodes will occur in the shortest path between them.
 *   1. Find LCA (Euler tour + sparse table RMQ).
 *   2. Find L = the length of the shortest path P / 2 = distance from U to LCA + distance from V to LCA / 2.
 *   3. Distance from U to median is L, from V to median is (P - L). Find one that is less then distance from the
 *   node to LCA. Let it be L'.
 *   4. The last thing is to find the L's ancestor of the node. It'll be the median we search for. It can be done
 *   with the help of jump pointers.
 */
public class Solution {

    private int n;

    private List<List<Integer>> tree;
    private List<Integer> euler;
    private List<Integer> eulerDepths;

    private boolean[] visited;
    private int[] depths;
    private int[] firstInEuler;

    private int[][] sparseTable;

    private int[][] jumpPointers;

    // constants
    private int[] logs;
    private int[] powersOfTwo = new int[20];

    private int nextInt() throws IOException {
        streamTokenizer.nextToken();
        return (int)streamTokenizer.nval;
    }

    private static StreamTokenizer streamTokenizer;

    public static void main(String[] args) throws IOException {
        new Solution().run();
    }

    private void run() throws IOException {
        streamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));

        buildTree();
        preprocess();
        printTestResults();
    }

    private void buildTree() throws IOException {
        n = nextInt();
        tree = new ArrayList<>(n);
        jumpPointers = new int[n][18];
        for (int i = 0; i < n; i++) {
            tree.add(new ArrayList<>());
        }
        for (int i = 1; i < n; ++i) {
            int parent = nextInt() - 1;
            tree.get(parent).add(i);
            jumpPointers[i][0] = parent;
        }
    }

    private void preprocess() {
        dfs();
        buildSparse(euler.size());
        fillJumpPointers();
    }

    private void dfs() {
        visited = new boolean[n];
        euler = new ArrayList<>();
        eulerDepths = new ArrayList<>();
        firstInEuler = new int[n];
        depths = new int[n];
        dfs(0, 0);
    }

    private void dfs(int v, int curDepth) {
        euler.add(v);
        eulerDepths.add(curDepth);
        if (firstInEuler[v] == 0) {
            firstInEuler[v] = euler.size() - 1;
        }
        visited[v] = true;
        depths[v] = curDepth;
        for (int i = 0; i < tree.get(v).size(); i++) {
            int child = tree.get(v).get(i);
            if (!visited[child]) {
                dfs(child, curDepth + 1);
                euler.add(v);
                eulerDepths.add(curDepth);
            }
        }
    }

    private void buildSparse(int size) {
        calculateConstants(size);
        buildTable(size);
    }

    private void calculateConstants(int size) {
        logs = new int[size + 1];
        int powerValue = 1, logValue = 0;
        for (int i = 1; i <= size; i++) {
            logs[i] = logValue - 1;
            if (powerValue == i) {
                powerValue *= 2;
                logs[i] = logValue;
                logValue++;
            }
        }

        powersOfTwo[0] = 1;
        for (int i = 1; i < powersOfTwo.length; i++) {
            powersOfTwo[i] = powersOfTwo[i - 1] * 2;
        }
    }

    private void buildTable(int size) {
        sparseTable = new int[size][logs[size] + 1];
        for (int i = 0; i < size; i++) {
            sparseTable[i][0] = i;
        }
        for (int j = 1; powersOfTwo[j] <= size; j++) {
            for (int i = 0; i + powersOfTwo[j] - 1 < size; i++) {
                if (eulerDepths.get(sparseTable[i][j - 1]) < eulerDepths.get(sparseTable[i + powersOfTwo[j - 1]][j - 1])) {
                    sparseTable[i][j] = sparseTable[i][j - 1];
                } else {
                    sparseTable[i][j] = sparseTable[i + powersOfTwo[j - 1]][j - 1];
                }
            }
        }
    }

    private int query(int u, int v) {
        int l = v - u;
        int k = logs[l];
        if (u == v) return u;
        if (eulerDepths.get(sparseTable[u][k]) > eulerDepths.get(sparseTable[v - powersOfTwo[k]][k])) {
            return sparseTable[v - powersOfTwo[k]][k];
        } else {
            return sparseTable[u][k];
        }
    }

    private int lca(int u, int v) {
        if (u == v) {
            return u;
        }
        if (firstInEuler[u] > firstInEuler[v]) {
            int temp = v;
            v = u;
            u = temp;
        }
        return euler.get(query(firstInEuler[u], firstInEuler[v]));
    }

    private int median(int u, int v) {
        u--;
        v--;
        int lca = lca(u, v);
        int fromUtoLCA = depths[u] - depths[lca];
        int fromVtoLCA = depths[v] - depths[lca];
        int pathLength = fromUtoLCA + fromVtoLCA;
        int fromUtoMed = pathLength / 2;
        int fromVtoMed = pathLength - fromUtoMed;
        if (fromUtoMed <= fromUtoLCA) {
            return getAncestorOfDepth(u, fromUtoMed) + 1;
        } else {
            return getAncestorOfDepth(v, fromVtoMed) + 1;
        }
    }

    private int getAncestorOfDepth(int u, int rank) {
        int i = 0, x = u;
        if (rank == 0) {
            return u;
        }
        while (rank > 0) {
            if ((rank & 1) == 1) {
                x = jumpPointers[x][i];
            }
            rank = rank >> 1;
            i++;
        }
        return x;
    }

    private void fillJumpPointers() {
        for (int j = 1; j <= logs[n] + 1; j++) {
            for (int i = 1; i < n; i++) {
                jumpPointers[i][j] = jumpPointers[jumpPointers[i][j - 1]][j - 1];
            }
        }
    }

    private void printTestResults() throws IOException {
        int testsCount = nextInt();
        for (int i = 0; i < testsCount; i++) {
            printTestResult(nextInt(), nextInt());
        }
    }

    private void printTestResult(int a, int q) {
        long sum = 0;
        for (int i = 1; i <= q; i++) {
            a = median(a, 1 + (i - 1) % n);
            sum += a;
        }
        System.out.println(sum);
    }
}