## new SpringApplication(sources)
1. 判断是否为web环境
2. 读取`META-INF/spring.factories`配置文件
3. 实例化实例化springFactories
4. 设置监听
## run(args)
1. 开启秒表计时监控
2. 设置[headless模式](https://www.oracle.com/technical-resources/articles/javase/headless.html)
3. 获取所有的SpringApplicationRunListeners（监听程序启动的，可以在这里做一些自己的事情），然后调用这些监听`listeners.starting()`
4. 实例化一个应用参数实例（就是将`main`方法的参数包装成`DefaultApplicationArguments`）
5. 准备环境（`Environment`），这里面有很多操作 `prepareEnvironment(listeners,applicationArguments)`
6. 打印banner（就是控制台上面spring的那个logo）`printBanner(environment)`
7. ***创建spring上下文*** `createApplicationContext`——重点
8. 创建一个故障分析器`new FailureAnalyzers(context)`
9. 准备上下文`prepareContext(context, environment, listeners, applicationArguments,printedBanner)`——重点
10. 刷新上下文`refreshContext(context)`——重点
11. 刷新上下文后的后置处理`afterRefresh(context, applicationArguments)`
12. 主程序中的run方法完成后再次调用第3步的监听，`listeners.finished(context, null)`
13. 停止秒表