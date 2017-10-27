package ru.rvsosn.eubmstubot.eubmstu;

public interface ITask<T> {

    T execute(EUBmstuApiExecutor executor, Context driver);
}
