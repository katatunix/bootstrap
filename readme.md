# A Bootstrapping Method for Automatically Generating Extraction Patterns from Operational Logs

## Abstract

Operational Intelligence usually requires extracting information from operational logs by writing regular expressions as extraction patterns. We present a method that automatically generates such regular expressions. As input, our technique requires only a small set of seed words appearing in a log repository. The output is a set of regular expressions which is capable of extracting relevant words from the log repository. We define relevant words as the ones that belong the same category with the seed words. Our algorithm is inspired from the _mutual bootstrapping_ method of Machine Learning and Information Extraction. To demonstrate the feasibility of the algorithm, we implemented a complete prototype supporting a wide range of use cases. We experimented this prototype on several large repositories of logs in the _syslog_ and _nagios_ formats. The prototype produced high-quality output for many cases under experimentation.

## How to use the F# prototype

* Install .NET framework 4.5 or newer, macOS users are recommended to install the Mono framework which is an implementation of .NET framework on `*nix` systems.

#### For end-users
* Open Command Prompt (Windows) or Terminal (macOS).
* Change current directory to `/fsharp/Bootstrap.Prototype/build`.
* Execute the file `Bootstrap.Prototype.exe`.
* macOS users would execute `mono Bootstrap.Prototype.exe` instead.

#### For developers
* Install [Visual Studio 2017](https://docs.microsoft.com/en-us/visualstudio/install/install-visual-studio).
* Open the file `/fsharp/Bootstrap.sln`.

## How to use the Java prototype

* Install Java 8 or newer.

#### For end-users
* Open Command Prompt (Windows) or Terminal (macOS)
* Change current directory to `/java/build`.
* Run `java -jar bootstrap.jar`.
* Increasing the heap size of JVM when running the prototype is recommended. For example, `java -Xmx3g -jar bootstrap.jar` would allow a heap capable of 3 GB.

#### For developers
* Install [IntelliJ IDEA the Java IDE]( https://www.jetbrains.com/idea/).
* Run IDEA and open the project `/java/bootstrap.iml`.
* Remember to select JDK 8 for the project.

## Prototype usage

`list` list all repos and their tasks

`crepo <repoName> <logFile>` create a new repo with initial logs

`delrepo <repoId>` delete repo and all of its tasks

`renrepo <repoId> <newRepoName>` rename repo

`viewrepo <repoId>` view content of repo

`al <repoId> <logFile>` add logs to repo

`rl <repoId> <logFile>` remove logs from repo

`cl <repoId>` clear all logs of repo

`ctask <repoId> <taskName>` create a new task for a repo

`deltask <taskId>` delete task

`rentask <taskId> <newTaskName>` rename task

`viewtask <taskId> [full | full logs]` view content of task
* `full` show relation between seed patterns and seed words
* `full logs` related logs will be shown

`aw <taskId> <word1> [word2] ...` add seed words

`fw <taskId> <word1> [word2] ...` forbid words

`rw <taskId> <word1> [word2] ...` remove words

`cw <taskId>` clear all words

`ap <taskId> <pattern1> [pattern2] ...` add seed patterns

`fp <taskId> <pattern1> [pattern2] ...` forbid patterns

`rp <taskId> <pattern1> [pattern2] ...` remove patterns

`cp <taskId>` clear all patterns

`up <taskId> <IterNum> <Threshold> <BestWordNum> <TimeoutSec>` update param
* `IterNum [int]` max number of iterations
* `Threshold [float]` patterns with score lower than this are ignored
* `BestWordNum [int]` max number of best words selected for each iteration
* `TimeoutSec [int]` timeout of generating patterns each iteration, zero means no timeout

`run <taskId>` run a task
