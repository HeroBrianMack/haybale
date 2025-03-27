package com.t2pellet.haybalelib.services;

public interface ISidedExecutor {

    void scheduleClient(Runnable runnable);

    void scheduleServer(Runnable runnable);

    void scheduleServer(int ticks, Runnable runnable);

}
