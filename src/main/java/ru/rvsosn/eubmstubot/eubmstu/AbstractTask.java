package ru.rvsosn.eubmstubot.eubmstu;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractTask<T> implements ITask<T> {
    private Deque<String> urlStack = new ArrayDeque<>();

    @Override
    public abstract T execute(EUBmstuApiExecutor executor, Context context);

    public <S> S executeOther(EUBmstuApiExecutor executor, Context context, ITask<S> task) {
        urlStack.push(context.getDriver().getCurrentUrl());
        S result = executor.executeTask(task);
        context.getDriver().get(urlStack.pop());
        return result;
    }
}
