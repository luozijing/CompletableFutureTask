package cf;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class CFUtil {
    public CFUtil() {
    }

    public static <TaskType, Object> CompletableFuture<Map<TaskType, Object>> sequenceMap(Collection<CompletableFuture<Map<TaskType, Object>>> completableFutures, BinaryOperator<Object> mergeFunction) {
        return CompletableFuture.allOf((CompletableFuture[])completableFutures.toArray(new CompletableFuture[0])).thenApply((v) -> {
            System.out.println("thenApply 执行线程：" + Thread.currentThread().getName());
            return (Map)completableFutures.stream().map(CompletableFuture::join).flatMap((map) -> {
                return map.entrySet().stream();
            }).collect(Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction));
        });
    }

    public static Throwable extractRealException(Throwable throwable) {
        return (throwable instanceof CompletionException || throwable instanceof ExecutionException) && throwable.getCause() != null ? throwable.getCause() : throwable;
    }

    public static enum TaskType {
        ITEM,
        INVENTORY,
        PROMOTION;

        private TaskType() {
        }
    }
}