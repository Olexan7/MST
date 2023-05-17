import java.util.*;
import java.util.concurrent.*;


class ParallelMinimumSpanningTree {
    private int k;
    private int[][] graph;
    private Set<Integer> visited;
    private List<Integer> result;

    public ParallelMinimumSpanningTree(int k, int[][] graph) {
        this.k = k;
        this.graph = graph;
        visited = new HashSet<>();
        result = new ArrayList<>();
    }

    public List<Integer> findMinimumSpanningTree() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < k; i++) {
            final int index = i;
            executor.execute(() -> {
                int minWeight = Integer.MAX_VALUE;
                int minNode = -1;
                for (int j = 0; j < graph.length; j++) {
                    if (!visited.contains(j) && graph[j][index] < minWeight) {
                        minWeight = graph[j][index];
                        minNode = j;
                    }
                }
                synchronized (result) {
                    result.add(minNode);
                    visited.add(minNode);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        return result;
    }

    public static void main(String[] args) {
        int[][] graph = {
                {0, 2, 0, 6, 0},
                {2, 0, 3, 8, 5},
                {0, 3, 0, 0, 7},
                {6, 8, 0, 0, 9},
                {0, 5, 7, 9, 0}
        };
        System.out.println("Веса ребер графа из " + graph.length + "вершин в виде матрицы весов");
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                System.out.print(graph[i][j] + "\t");
            }
            System.out.println();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество узлов k: ");
        int k = scanner.nextInt();
        System.out.println("");

        ParallelMinimumSpanningTree mst = new ParallelMinimumSpanningTree(k, graph);
        try {
            List<Integer> result = mst.findMinimumSpanningTree();
            System.out.println("Минимальное остевое дерево, которое имеет k="+ k + " вершин: ");
            for (int node : result) {
                    System.out.print(node + " -> ");
            }
        } catch (InterruptedException e) {
            System.out.println("k > числа вершин: " + k);
            e.printStackTrace();
        }
    }
}
