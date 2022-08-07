package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.DoubleFunction;
import java.util.stream.Stream;

class IntegralProgram {

    public static final int STEPS = 10000000;
    public static final int TASKS = 20;

    public static double singleThread(DoubleFunction<Double> f, double a, double b, int steps) {
        double h = (b - a) / steps;
        double summa = 0d;

        for(int i = 0; i < steps; i++) {
            double x = a + h*i + h / 2;
            double y = f.apply(x);
            summa += y*h;
        }
        return summa;
    }

    public static double multiThread(DoubleFunction<Double> f, double a, double b) throws InterruptedException, ExecutionException {
        ExecutorService exec = Executors.newFixedThreadPool(TASKS);
        double h = (b - a) / TASKS;

        Future<Double>[] future = new Future[TASKS];
        for (int i = 0; i < TASKS; i++) {
            final double ax = a + h * i;
            final double bx = ax + h;
            future[i] = exec.submit(() -> singleThread(f, ax, bx, STEPS / TASKS));
        }

        double sum = 0;
        for(Future<Double> task : future) {
            sum += task.get();
        }

        exec.shutdown();
        return sum;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long t1 = System.currentTimeMillis();
        double r1 = singleThread(Math::sin, 0, Math.PI/2, STEPS);
        long t2 = System.currentTimeMillis();
        System.out.printf("Single Thread: %f Time: %d\n", r1, t2-t1);



        long t3 = System.currentTimeMillis();
        double r2 = multiThread(Math::sin, 0, Math.PI/2);
        long t4 = System.currentTimeMillis();
        System.out.printf("Single Thread: %f Time: %d\n", r2, t4-t3);
    }


}
