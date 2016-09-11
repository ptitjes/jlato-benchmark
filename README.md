# jlato-benchmark
Benchmark code to test JLaTo against Javac and JavaParser

## How-to run some benchmarks

Be aware that the benchmarks' executions are really long. So you should run them during the night.

You can browse some preliminary results [here](./results.pdf).

### To run parse benchmark of different libraries (~1h)

The libraries are automatically downloaded from Maven Central.

```sh
mvn validate clean install
java -jar target/benchmarks.jar "PartiallyParsing\."
```

### To benchmark parsing the OpenJDK (~1h)

Fist change the location of the OpenJDK in the file `PartiallyParsingJDK` before rebuilding. 

```sh
mvn validate clean install
java -jar target/benchmarks.jar "PartiallyParsingJDK"
```

