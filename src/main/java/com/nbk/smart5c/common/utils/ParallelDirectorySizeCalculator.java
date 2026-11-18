package com.nbk.smart5c.common.utils;

import lombok.var;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ParallelDirectorySizeCalculator {

    // 方法2.1: 使用并行流 (Java 8+)
    public static long getDirectorySizeParallel(Path path) throws IOException {
        try (var stream = Files.walk(path).parallel()) {
            return stream
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        }
    }

    // 方法2.2: 使用ForkJoinPool (更精细控制并行度)
    public static long getDirectorySizeForkJoin(Path startPath) {
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        try {
            SizeCalculatorTask task = new SizeCalculatorTask(startPath);
            return pool.invoke(task);
        } finally {
            pool.shutdown();
        }
    }

    static class SizeCalculatorTask extends RecursiveTask<Long> {
        private final Path path;

        public SizeCalculatorTask(Path path) {
            this.path = path;
        }

        @Override
        protected Long compute() {
            if (Files.isRegularFile(path)) {
                try {
                    return Files.size(path);
                } catch (IOException e) {
                    return 0L;
                }
            }

            try (var stream = Files.list(path)) {
                return stream
                        .map(subPath -> new SizeCalculatorTask(subPath).fork())
                        .mapToLong(ForkJoinTask::join)
                        .sum();
            } catch (IOException e) {
                return 0L;
            }
        }
    }
}