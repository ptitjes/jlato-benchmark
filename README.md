# jlato-benchmark
Benchmark code to test JLaTo against Javac and JavaParser

## How-to run some benchmarks

Be aware that the benchmarks' executions are really long. So you should run them during the night.

You can browse the latest results [here](./results/2016-09-12-05:49:49.pdf). 
Results obtained on a MacBookPro 11,4 (Intel Core i7 4980HQ @ 2.8GHz + SSD) running openjdk-1.8.0.92.

### To benchmark simple parsing of different libraries (~1h)

The libraries are automatically downloaded from Maven Central as part of the maven build.

```sh
mvn validate clean install
java -jar target/benchmarks.jar "LibsPartial"
```

This will produce a new PDF file in the `results` directory.

### To benchmark full parsing of different libraries (~45mn)

The libraries are automatically downloaded from Maven Central as part of the maven build.

```sh
mvn validate clean install
java -jar target/benchmarks.jar "LibsFull"
```

This will produce a new PDF file in the `results` directory.

### To benchmark parsing the OpenJDK (~2h)

The OpenJDK is automatically downloaded from java.net as part of the maven build.

```sh
mvn validate clean install
java -jar target/benchmarks.jar "JDK"
```

This will produce a new PDF file in the `results` directory.
