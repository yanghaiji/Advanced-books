## ExecutorService指南

### 1.概述
ExecutorService是JDK提供的框架，可简化异步模式下任务的执行。一般来说，
 ExecutorService自动提供线程池和API来为其分配任务。
 
### 2.实例化ExecutorService

### 2.1 执行者类的工厂方法

创建*ExecutorService*的最简单方法是使用*Executors*类的工厂方法之一。

例如，以下代码行将创建一个包含10个线程的线程池：

```
ExecutorService executor = Executors.newFixedThreadPool(``10``);
```

还有其他几种工厂方法可以创建满足特定用例的预定义ExecutorService。

### 2.2。直接创建一个ExecutorService

由于*ExecutorService*是接口，因此可以使用其所有实现的实例。您可以在*java.util.concurrent*包中选择几种实现，也可以创建自己的实现。

例如，*ThreadPoolExecutor*类具有一些构造函数，可用于配置执行程序服务及其内部池。

```
ExecutorService executorService = `` ``new` `ThreadPoolExecutor(``1``, ``1``, 0L, TimeUnit.MILLISECONDS,  `` ``new` `LinkedBlockingQueue());
```

您可能会注意到，上面的代码与工厂方法*newSingleThreadExecutor（）*的源代码非常相似*。*在大多数情况下，不需要详细的手动配置。

### 3.将任务分配给ExecutorService

*ExecutorService*可以执行*Runnable*和*Callable*任务。为了使本文简单，将使用两个原始任务。请注意，这里使用了lambda表达式，而不是匿名内部类：

```
Runnable runnableTask = () -> {``  ``try` `{``    ``TimeUnit.MILLISECONDS.sleep(``300``);``  ``} ``catch` `(InterruptedException e) {``    ``e.printStackTrace();``  ``}``};` `Callable callableTask = () -> {``  ``TimeUnit.MILLISECONDS.sleep(``300``);``  ``return` `"Task's execution"``;``};` `List> callableTasks = ``new` `ArrayList<>();``callableTasks.add(callableTask);``callableTasks.add(callableTask);``callableTasks.add(callableTask);
```

可以使用多种方法将任务分配给*ExecutorService*，其中包括*execute（）*（它是从*Executor*接口继承的*）*，还包括*commit（），invokeAny（）和invokeAll（）。*

在执行（）法是*无效的，*而且不给任何可能获得任务的执行结果或检查任务的状态（是否运行或执行）。

```
executorService.execute(runnableTask);
```

Submit（）向 *ExecutorService*提交 *Callable*或 *Runnable*任务，并返回 *Future*类型的结果。

```
Future future = `` ``executorService.submit(callableTask);
```

invokeAny（）将任务集合分配给 *ExecutorService，*从而使每个任务都被执行，并返回一个任务成功执行的结果（如果执行成功）*。*



```
String result = executorService.invokeAny(callableTasks);
```

invokeAll（）将任务集合分配给 *ExecutorService，*从而使每个任务都被执行，并以 *Future*类型的对象列表的形式返回所有任务执行的结果*。*

```
List> futures = executorService.invokeAll(callableTasks);
```

现在，在进一步讨论之前，必须讨论另外两件事：关闭*ExecutorService*和处理*Future*返回类型。

### 4.关闭执行器服务

通常，没有要处理的任务时，不会自动销毁*ExecutorService*。它将保持活力，并等待新的工作完成。

在某些情况下，这很有帮助；例如，如果某个应用需要处理不定期出现的任务，或者在编译时不知道这些任务的数量。

另一方面，一个应用程序可能已到达尽头，但不会停止，因为等待中的*ExecutorService*将导致JVM继续运行。

要正确关闭*ExecutorService*，我们有*shutdown（）*和*shutdownNow（）* API。

的***关断（）*** 方法不会导致的立即销毁*的ExecutorService。*这将使*ExecutorService*停止接受新任务，并在所有正在运行的线程完成其当前工作之后关闭。

```
executorService.shutdown();
```

该***shutdownNow时（）\***方法试图破坏*ExecutorService的*马上，但它并不能保证所有正在运行的线程将同时停止。此方法返回等待处理的任务列表。由开发人员决定如何处理这些任务。

```
List notExecutedTasks = executorService.shutDownNow();
```

关闭*ExecutorService*的一种好方法是将这两种方法与***awaitTermination（）\***方法结合使用。使用这种方法，*ExecutorService*将首先停止*执行*新任务，然后等待指定的时间段才能完成所有任务。如果该时间到期，则立即停止执行：

```
executorService.shutdown();``try` `{``  ``if` `(!executorService.awaitTermination(``800``, TimeUnit.MILLISECONDS)) {``    ``executorService.shutdownNow();``  ``} ``} ``catch` `(InterruptedException e) {``  ``executorService.shutdownNow();``}
```

### 5. 未来的界面

在*提交（）*和*的invokeAll（）*方法返回一个对象或类型的对象的集合*的未来*，这使我们能够得到一个任务的执行结果或检查任务的状态（是否运行或执行）。

在*未来的*接口提供了一个特殊的阻塞方法*的get（）* ，它返回的实际结果，*可调用*任务的执行或*无效*的情况下*可运行*任务。在任务仍在运行时调用*get（）*方法将导致执行阻塞，直到任务正确执行且结果可用为止。



```
Future future = executorService.submit(callableTask);``String result = ``null``;``try` `{``  ``result = future.get();``} ``catch` `(InterruptedException | ExecutionException e) {``  ``e.printStackTrace();``}
```

由于*get（）*方法造成的阻塞非常长，因此应用程序的性能可能会下降。如果结果数据不是至关重要的，则可以通过使用超时来避免此类问题：

```
String result = future.get(``200``, TimeUnit.MILLISECONDS);
```

如果执行时间长于指定的时间（在这种情况下为200毫秒），则将抛出*TimeoutException*。

的*isDone（）*方法可用于检查所分配的任务已经处理或没有。

在*未来的*界面还提供了任务执行的取消与*取消（）*方法，以及检查与消除*isCancelled（）*方法：

```
boolean` `canceled = future.cancel(``true``);``boolean` `isCancelled = future.isCancelled();
```

### 6. ScheduledExecutorService接口

该*ScheduledExecutorService的*运行一些预定义的延迟和/或定期后任务。再次，实例化*ScheduledExecutorService*的最佳方法是使用*Executors*类的工厂方法。

对于本节，将使用带有一个线程的*ScheduledExecutorService*：

```
ScheduledExecutorService executorService = Executors`` ``.newSingleThreadScheduledExecutor();
```

要在固定延迟后安排单个任务的执行，请使用*ScheduledExecutorService*的*schedule（）*方法。有两种*Scheduled（）*方法可让您执行*Runnable*或*Callable*任务：

```
Future resultFuture = `` ``executorService.schedule(callableTask, ``1``, TimeUnit.SECONDS);
```

使用*scheduleAtFixedRate（）*方法可以在固定延迟后定期执行任务。上面的代码在执行*callableTask*之前会延迟一秒钟。

下面的代码块将在100毫秒的初始延迟后执行任务，此后，它将每450毫秒执行一次相同的任务。如果处理器需要比*scheduleAtFixedRate（）*方法的*period*参数更多的时间来执行分配的任务，则*ScheduledExecutorService*将等到当前任务完成后再开始下一个任务：

```
Future resultFuture = service`` ``.scheduleAtFixedRate(runnableTask, ``100``, ``450``, TimeUnit.MILLISECONDS);
```

如果必须在任务迭代之间设置固定的长度延迟，则应使用*scheduleWithFixedDelay（）*。例如，以下代码将保证当前执行的结束与另一个执行的开始之间有150毫秒的暂停。

```
service.scheduleWithFixedDelay(task, ``100``, ``150``, TimeUnit.MILLISECONDS);
```

根据*scheduleAtFixedRate（）*和*scheduleWithFixedDelay（）*方法合同，任务的定期执行将在*ExecutorService*终止时终止，或者如果在任务执行期间引发异常，则该任务的定期执行将结束*。*



### 7.ExecutorService与Fork  Join

Java 7发行后，许多开发人员决定将*ExecutorService*框架替换为fork / join框架。但是，这并不总是正确的决定。尽管使用的简便性和与fork / join相关的频繁的性能提升，开发人员对并发执行的控制量也有所减少。

*ExecutorService*使开发人员能够控制所生成线程的数量以及应由单独的线程执行的任务的粒度。*ExecutorService*的最佳用例是按照“一个线程处理一个任务”的方案处理独立任务，例如事务或请求。

相反，根据Oracle的文档，fork / join旨在加快工作速度，该工作可以递归分解为较小的部分。
