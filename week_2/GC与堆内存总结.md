# GC与堆内存
## 1、GC 
GC就是垃圾回收机制, 在系统运行的时候，会产生一些无用的对象，他们占据着一部分内存， 所以需要GC去进行内存的释放，简而言之就是找到已经无用的对象,并把这些对象占用的空间收回使其可以重新利用.
#### 1.1 串行 GC 
通过 JVM 参数 `"-XX:+UseSerialGC"` 来配置* GC 算法 & 堆内存分区    * 年轻代：mark-copy    * 老年代：mark-sweep-compact* 适用场景：堆内存较小的JVM，且是单核CPU时
#### 1.2 并行 GC 
通过 JVM 参数 `"-XX:+UseParallelGC", "-XX:+UseParallelOldGC", "-XX:+UseParallelGC 
-XX:+UseParallelOldGC"` 来配置* GC 算法 & 堆内存分区    * 年轻代：mark-copy    * 老年代：mark-sweep-compact* 特点：线程数可指定* 适用场景：需要增加吞吐量的系统，且是多核CPU时
#### 1.3 CMS GC
通过 JVM 参数 `"-XX:+UseConcMarkSweepGC"` 来配置 GC 算法 & 堆内存分区     
年轻代：mark-copy（可通过参数"-XX:+UserParNewGC"对年轻代进行并行GC）     
老年代：mark-sweep
特点：大部分业务线程和GC线程可以并发执行（业务线程的连续性）
##### 阶段 1: Initial Mark（初始标记）
##### 阶段 2: Concurrent Mark（并发标记）
##### 阶段 3: Concurrent Preclean（并发预清理）
##### 阶段 4: Final Remark（最终标记）
##### 阶段 5: Concurrent Sweep（并发清除）
##### 阶段 6: Concurrent Reset（并发重置）
CMS 垃圾收集器在减少停顿时间上做了很多复杂而有用的
工作，用于垃圾回收的并发线程执行的同时，并不需要暂停
应用线程。 当然，CMS 也有一些缺点，其中最大的问题就
是老年代内存碎片问题（因为不压缩），在某些情况下 GC
会造成不可预测的暂停时间，特别是堆内存较大的情况下。
#### 1.4 G1 GC 
#### **配置参数**

| 参数 | 类型 | 占Java Heap的大小 | 备注 |
| --- | --- | --- | --- |
|G1NewSizePercent|-XX |5%|初始年轻代|
|G1MaxNewSizePercent|-XX|60%|最大年轻代|
|G1HeapRegionSize  |-XX |--|设置每个 Region 的大小，单位：MB，为 1、2、4、8、16、32 中的某个值，默认是堆内存的1/2000 |
| ConcGCThreads|-XX|默认是 Java 线程的 1/4 |减少这个参数的数值可能会提升并行回收的效率，提高系统内部吞吐量。如果这个数值过低，参与回收垃圾的线程不足，也会导致并行回收机制耗时加长|
|+InitiatingHeapOccupancyPercent| -XX |默认为 Java Heap的 45% |（简称 IHOP）：G1 内部并行回收循环启动的阈值,这个可以理解为老年代使用大于等于 45% 的时候，JVM 会启动垃圾回收。这个值非常重要，它决定了在什么时间启动老年代的并行回收|
|G1HeapWastePercent|-XX|5%|G1停止回收的最小内存大小，默认是堆大小的 5%。GC 会收集所有的 Region 中的对象，但是如果下降到了 5%，就会停下来不再收集了。就是说，不必每次回收就把所有的垃圾都处理完，可以遗留少量的下次处理，这样也降低了单次消耗的时间|
|G1MixedGCCountTarget|-XX|默认值是8个 |设置并行循环之后需要有多少个混合 GC 启动，默认值是 8 个。老年代 Regions的回收时间通常比年轻代的收集时间要长一些。所以如果混合收集器比较多，可以允许 G1 延长老年代的收集时间|
|+G1PrintRegionLivenessInfo|-XX|--|这个参数需要和 -XX:+UnlockDiagnosticVMOptions 配合启动，打印 JVM 的调试信息，每个Region 里的对象存活信息|
|G1ReservePercent|-XX|默认值是堆空间的 10%|G1 为了保留一些空间用于年代之间的提升，因为大量执行回收的地方在年轻代（存活时间较短），所以如果你的应用里面有比较大的堆内存空间、比较多的大对象存活，这里需要保留一些内存|
|+G1SummarizeRSetStats|-XX|--|也是一个 VM 的调试信息。如果启用，会在 VM 退出的时候打印出 Rsets 的详细总结信息。如果启用 -XX:G1SummaryRSetStatsPeriod 参数，就会阶段性地打印 Rsets 信息|
|+G1TraceConcRefinement|-XX|--|也是一个 VM 的调试信息，如果启用，并行回收阶段的日志就会被详细打印出来|
|+GCTimeRatio|-XX|默认是 9|计算花在 Java 应用线程上和花在 GC 线程上的时间比率跟新生代内存的分配比例一致。这个参数主要的目的是让用户可以控制花在应用上的时间，G1 的计算公式是 100/（1+GCTimeRatio）。这样如果参数设置为 9，则最多 10% 的时间会花在 GC 工作上面。Parallel GC 的默认值是 99，表示 1% 的时间被用在 GC 上面，这是因为 Parallel GC贯穿整个 GC，而 G1 则根据 Region 来进行划分，不需要全局性扫描整个内存堆。|
|+UseStringDeduplication|-XX|--|手动开启 Java String 对象的去重工作，这个是 JDK8u20 版本之后新增的参数，主要用于相同String 避免重复申请内存，节约 Region 的使用。|
|MaxGCPauseMills|-XX|默认值是 200 毫秒|预期 G1 每次执行 GC 操作的暂停时间，单位是毫秒，默认值是 200 毫秒，G1 会尽量保证控制在这个范围|
## 2、堆内存

堆内存是所有线程共用的内存空间，JVM 将Heap 内存分为**年轻代**（Young generation）和**老年代**（Old generation, 也叫 Tenured）两部分。年轻代还划分为 3 个内存池，新生代（Edenspace）和存活区（Survivor space）。

年轻代：Eden + S0 + S1；特点：Eden, S0, S1中只有两个区一致有数据

老年代特点：对象存活过一定GC周期后，被移动到老年代。老年代默认都是存活对象。


