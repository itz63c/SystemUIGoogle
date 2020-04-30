package com.android.systemui;

import android.util.Pools.SynchronizedPool;
import android.view.SurfaceControl.Transaction;

public class TransactionPool {
    private final SynchronizedPool<Transaction> mTransactionPool = new SynchronizedPool<>(4);

    TransactionPool() {
    }

    public Transaction acquire() {
        Transaction transaction = (Transaction) this.mTransactionPool.acquire();
        return transaction == null ? new Transaction() : transaction;
    }

    public void release(Transaction transaction) {
        if (!this.mTransactionPool.release(transaction)) {
            transaction.close();
        }
    }
}
