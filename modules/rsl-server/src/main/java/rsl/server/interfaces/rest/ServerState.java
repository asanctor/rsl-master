package rsl.server.interfaces.rest;

import rsl.server.ServerResponse;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerState {

    public ServerResponse response;

    private final Lock lock = new ReentrantLock();
    private final Condition resultFetched  = lock.newCondition();
    private final Condition awaitStarted  = lock.newCondition();

    private boolean isWaiting = false;
    private boolean hasResult = false;

    // make sure to call awaitresult() before setresponse() or this method will block forever
    public void setResponse(ServerResponse response)
    {
        lock.lock();
        try {
            while(!isWaiting)
            {
                awaitStarted.await();
            }
            this.response = response;
            this.hasResult = true;
            this.isWaiting = true;
            resultFetched.signalAll();
        } catch (InterruptedException e) {

        } finally {
            lock.unlock();
        }
    }

    public void awaitResult() throws InterruptedException {
        lock.lock();
        try {
            this.isWaiting = true;
            awaitStarted.signalAll();
            this.hasResult = false;
            while (!hasResult) {
                resultFetched.await();
            }
        } catch (InterruptedException e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

}
