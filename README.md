# jlato-benchmark
Benchmark code to test JLaTo against Javac and JavaParser

## How-to run some benchmarks

Be aware that the benchmarks' executions are really long. So you should run them during the night.

You can browse the latest results [here](./results/2016-09-12-05:49:49.pdf). 
Results obtained on a MacBookPro 11,4 (Intel Core i7 4980HQ @ 2.8GHz + SSD) running openjdk-1.8.0.92.

### To run parse benchmark of different libraries (~1h)

The libraries are automatically downloaded from Maven Central.

```sh
mvn validate clean install
java -jar target/benchmarks.jar org.jlato.Runner "PartiallyParsing\."
```

This will produce a new PDF file in the `results` directory.

### To benchmark parsing the OpenJDK (~2h)

Fist change the location of the OpenJDK in the file `PartiallyParsingJDK` before rebuilding. 

```sh
mvn validate clean install
java -jar target/benchmarks.jar org.jlato.Runner "PartiallyParsingJDK"
```

This will produce a new PDF file in the `results` directory.
