# just-now
`just-now` is an implementation for polling-based futures for Java based on Rust. It provides the basic `Future` interface, `Waker` to avoid spinning, various combinator implementations as well as 3 executor implementations for driving future execution.

Documentation work in progress! :)

This library can be easily added to your workspace with Gradle:
```gradle
repositories {
  maven { url = 'https://maven.gegy1000.net/' }
}

dependencies {
  implementation 'net.gegy1000:just-now:0.1.0-SNAPSHOT'
}
```

#### Examples
```java
public static void main(String[] args) {
    Executor executor = Executors.newSingleThreadExecutor();
    
    // count to 10 off-thread
    Future<String> countFuture = Future.spawnBlocking(executor, () -> {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
            }
            return "I counted to 10!";
        } catch (InterruptedException e) {
            return "Failed to count to 10 :(";
        }
    });

    // once the count future is complete, map the result
    Future<String> greetingFuture = countFuture.map(message -> {
        return "Hello world! " + message;
    });
    
    // block until the future resolves
    String greeting = CurrentThreadExecutor.blockOn(greetingFuture);
    System.out.println(greeting);
}
```

#### Roadmap
 - [ ] Documentation
 - [ ] Better standards & combinators for error handling
 - [ ] Async timers
