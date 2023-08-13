import cf.CFUtil;
import cf.CFUtil.TaskType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        ExecutorService threadPool1 = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
        List<CompletableFuture<Map<TaskType, Object>>> asyncList = new ArrayList<>();
        CompletableFuture<Map<TaskType, Object>> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 执行线程：" + Thread.currentThread().getName());
            HashMap<TaskType, Object> res = new HashMap<>();
            List<String> test1 = new ArrayList<>();
            test1.add("1");
            res.put(TaskType.ITEM, test1);
            return res;
        }, threadPool1);
        asyncList.add(future1);
        CompletableFuture<Map<TaskType, Object>> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 执行线程：" + Thread.currentThread().getName());
            HashMap<TaskType, Object> res = new HashMap();
            List<Integer> test2 = new ArrayList();
            test2.add(2);
            res.put(TaskType.INVENTORY, test2);
            int a = 1 / 0;
            return res;
        }, threadPool1);
        asyncList.add(future2);
        CompletableFuture<Map<TaskType, Object>> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 执行线程：" + Thread.currentThread().getName());
            HashMap res = new HashMap();

            try {
                Thread.sleep(5L);
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }

            List<Double> test3 = new ArrayList();
            Map<String, List<Double>> testMap = new HashMap();
            test3.add(2.0D);
            testMap.put("1", test3);
            res.put(TaskType.PROMOTION, testMap);
            return res;
        }, threadPool1);
        asyncList.add(future3);
        CompletableFuture res = CFUtil.sequenceMap(asyncList, (v1, v2) -> {
            return v1;
        });

        try {
            Map<TaskType, Object> taskRes = (Map)res.get(5L, TimeUnit.SECONDS);
            List<String> task1Res = (List)taskRes.get(TaskType.ITEM);
            List<Integer> task2Res = (List)taskRes.get(TaskType.INVENTORY);
            Map<String, List<Double>> task3Res = (Map)taskRes.get(TaskType.PROMOTION);
            System.out.println(task1Res.toString() + task2Res.toString() + task3Res.get("1"));
        } catch (ExecutionException | TimeoutException | InterruptedException var11) {
            System.out.println(CFUtil.extractRealException(var11));
        } catch (Throwable var12) {
            System.out.println(var12);
        }

        threadPool1.shutdown();
    }
}