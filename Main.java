import cf.CFUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {



    public static void main(String[] args) {
        ExecutorService threadPool1 = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));

        List<CompletableFuture<Map<CFUtil.TaskType, Object>>> asyncList = new ArrayList<>();
        CompletableFuture<Map<CFUtil.TaskType, Object>> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 执行线程：" + Thread.currentThread().getName());
            HashMap<CFUtil.TaskType, Object> res = new HashMap<>();
            List<String> test1 = new ArrayList<>();
            test1.add("1");
            res.put(CFUtil.TaskType.ITEM, test1);
            return res;
        }, threadPool1);

        asyncList.add(future1);

        CompletableFuture<Map<CFUtil.TaskType, Object>> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 执行线程：" + Thread.currentThread().getName());
            HashMap<CFUtil.TaskType, Object> res = new HashMap<>();
            List<Integer> test2 = new ArrayList<>();
            test2.add(2);
            res.put(CFUtil.TaskType.INVENTORY, test2);
            int a = 1/0;
            return res;
        }, threadPool1);
        asyncList.add(future2);

        CompletableFuture<Map<CFUtil.TaskType, Object>> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 执行线程：" + Thread.currentThread().getName());
            HashMap<CFUtil.TaskType, Object> res = new HashMap<>();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Double> test3 = new ArrayList<>();
            Map<String, List<Double>> testMap = new HashMap<>();
            test3.add(2.0);
            testMap.put("1", test3);
            res.put(CFUtil.TaskType.PROMOTION, testMap);
            return res;
        }, threadPool1);
        asyncList.add(future3);

        CompletableFuture<Map<CFUtil.TaskType, Object>> res = CFUtil.sequenceMap(asyncList, (v1, v2) -> v1);
        try {
            Map<CFUtil.TaskType, Object> taskRes = res.get(5, TimeUnit.SECONDS);
            List<String> task1Res = (List<String>) taskRes.get(CFUtil.TaskType.ITEM);
            List<Integer> task2Res = (List<Integer>) taskRes.get(CFUtil.TaskType.INVENTORY);
            Map<String, List<Double>> task3Res = (Map<String, List<Double>>) taskRes.get(CFUtil.TaskType.PROMOTION);
            System.out.println(task1Res.toString()+task2Res.toString()+task3Res.get("1"));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println(CFUtil.extractRealException(e));
        } catch (Throwable e) {
            System.out.println(e);
        }
        threadPool1.shutdown();
        //System.exit(0);
    }

}
