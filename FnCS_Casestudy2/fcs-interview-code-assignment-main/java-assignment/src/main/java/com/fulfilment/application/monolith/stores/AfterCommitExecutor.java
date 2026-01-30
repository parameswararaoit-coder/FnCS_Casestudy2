package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@ApplicationScoped
public class AfterCommitExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(AfterCommitExecutor.class);

    TransactionSynchronizationRegistry txSyncRegistry;

    @Inject
    public AfterCommitExecutor(TransactionSynchronizationRegistry txSyncRegistry) {
        this.txSyncRegistry = Objects.requireNonNull(txSyncRegistry, "txSyncRegistry");
    }

    public void runAfterCommit(Runnable action) {
        if (action == null) {
            return;
        }

        try {
            int status = txSyncRegistry.getTransactionStatus();
            if (status == Status.STATUS_ACTIVE) {
                try {
                    txSyncRegistry.registerInterposedSynchronization(new CommitSynchronization(action));
                    return;
                } catch (Exception e) {
                    LOG.warn("Failed to register interposed synchronization, executing action immediately", e);
                }
            }
        } catch (IllegalStateException e) {
            // No transaction associated -> fall through to immediate execution
        } catch (Exception e) {
            LOG.warn("Error while checking transaction status, executing action immediately", e);
        }

        safeRun(action);
    }

    private void safeRun(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.error("After-commit action threw an exception", e);
        }
    }

    private static class CommitSynchronization implements Synchronization {
        private final Runnable action;

        CommitSynchronization(Runnable action) {
            this.action = action;
        }

        @Override
        public void beforeCompletion() {
            // no-op
        }

        @Override
        public void afterCompletion(int completionStatus) {
            if (completionStatus == Status.STATUS_COMMITTED) {
                try {
                    action.run();
                } catch (Exception e) {
                    LoggerFactory.getLogger(AfterCommitExecutor.class).error("After-commit action failed", e);
                }
            } else {
                LoggerFactory.getLogger(AfterCommitExecutor.class)
                        .debug("Transaction completed with status {}, skipping after-commit action", completionStatus);
            }
        }
    }
}
