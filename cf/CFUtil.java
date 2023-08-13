package cf;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;


public class CFUtil {


    /**
     * 同步等待completableFutures 结果
     * 将List<CompletableFuture<Map<TaskType, Object>> 转为 CompletableFuture<Map<TaskType, Object>>
     * 此时，如果CompletableFuture中的业务操作已经执行完毕并返回，则该thenApply直接由当前main线程执行；否则，将会由执行CompletableFuture业务操作的线程执行
     * @param mergeFunction 自定义key冲突时的merge策略
     * @param completableFutures 需要执行的异步任务，最好有自己的超时保护，防止线程不释放，引起oom
     * CompletableFuture 提供了 join() 方法，它的功能和 get() 方法是一样的，都是阻塞获取值，它们的区别在于 join() 抛出的是 unchecked Exception，这里需要处理异常
     */
    public static <TaskType, Object> CompletableFuture<Map<TaskType, Object>> sequenceMap(
            Collection<CompletableFuture<Map<TaskType, Object>>> completableFutures, BinaryOperator<Object> mergeFunction) {
        return CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture<?>[0]))
                .thenApply(v -> {
                    System.out.println("thenApply 执行线程：" + Thread.currentThread().getName());
                    return completableFutures.stream().map(CompletableFuture::join)
                            .flatMap(map -> map.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
                });
    }

    public static Throwable extractRealException(Throwable throwable) {
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }

    public enum TaskType {
        ITEM,
        INVENTORY,
        PROMOTION;

        TaskType() {
        }

    }





}
