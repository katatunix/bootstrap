namespace Bootstrap.Prototype

open Bootstrap.Core
open System.Collections.Generic
open NghiaBui.Common

module UI =

    let printIfErrorAndMakeExitCode res =
        match res with
        | Ok _ -> 0
        | Error msg -> printfn "Error: %s" msg; 1

    let printUsage () =
        printfn "Usage: Bootstrap.Prototype.exe <action> [parameters]"
        printfn ""
        printfn "list: list all repos and their tasks"
        printfn ""
        printfn "crepo <repoName> <logFile>: create a new repo with initial logs"
        printfn "delrepo <repoId>: delete repo and all of its tasks"
        printfn "renrepo <repoId> <newRepoName>: rename repo"
        printfn "viewrepo <repoId>: view content of repo"
        printfn "al <repoId> <logFile>: add logs to repo"
        printfn "rl <repoId> <logFile>: remove logs from repo"
        printfn "cl <repoId>: clear all logs of repo"
        printfn ""
        printfn "ctask <repoId> <taskName>: create a new task for a repo"
        printfn "deltask <taskId>: delete task"
        printfn "rentask <taskId> <newTaskName>: rename task"
        printfn "viewtask <taskId> [full | full logs]: view content of task"
        printfn "    full      : show relation between seed patterns and seed words"
        printfn "    full logs : related logs will be shown"
        printfn ""
        printfn "aw <taskId> <word1> [word2] ...: add seed words"
        printfn "fw <taskId> <word1> [word2] ...: forbid words"
        printfn "rw <taskId> <word1> [word2] ...: remove words"
        printfn "cw <taskId>: clear all words"
        printfn ""
        printfn "ap <taskId> <pattern1> [pattern2] ...: add seed patterns"
        printfn "fp <taskId> <pattern1> [pattern2] ...: forbid patterns"
        printfn "rp <taskId> <pattern1> [pattern2] ...: remove patterns"
        printfn "cp <taskId>: clear all patterns"
        printfn ""
        printfn "up <taskId> <IterNum> <Threshold> <BestWordNum> <TimeoutSec>: update param"
        printfn "    IterNum        [int]   : max number of iterations"
        printfn "    Threshold      [float] : patterns with score lower than this are ignored"
        printfn "    BestWordNum    [int]   : max number of best words selected for each iteration"
        printf  "    TimeoutSec     [int]   : timeout of generating patterns each iteration,"
        printfn " zero means no timeout"
        printfn ""
        printfn "run <taskId>: run a task"

    let printListAll (repos, tasks) =
        for (repoId, repoName) in repos do
            printfn "[%d] REPO: %s" repoId repoName
            for (taskId, taskName, r) in tasks do
                if r = repoId then
                    printfn "    [%d] TASK: %s" taskId taskName

    let printCreateRepoSuccess repoId =
        printfn "Create repo successfully with id = %d" repoId

    let printCreateTaskSuccess taskId =
        printfn "Create task successfully with id = %d" taskId

    let printLoadingRepo id =
        printfn "LOADING REPO [%d] ..." id
        id

    let printUpdatingRepo id =
        printfn "UPDATING REPO [%d] ..." id

    let printSavingRepo id =
        printfn "SAVING REPO [%d] ..." id
        id

    let printLoadingTask id =
        printfn "LOADING TASK [%d] ..." id
        id

    let printUpdatingTask id =
        printfn "UPDATING TASK [%d] ..." id

    let printSavingTask id =
        printfn "SAVING TASK [%d] ..." id

    let printClearingRepo id =
        printfn "CLEARING REPO [%d] ..." id

    let printRepo (repo : Repo) =
        printfn "REPO: %s" repo.Name
        printfn "%d log events:" repo.Logs.Count
        for log in repo.Logs do
            printfn "%s" log

    let printTask (name : string, wordPool : Word Pool, patternPool : Pattern Pool, param : Param) =
        printfn "TASK: %s\n" name

        let printWords name (words : IHS<Word>) =
            printfn "%d %s words: %s" words.Count name (String.concat ", " words)
            printfn ""

        printWords "seed" wordPool.Seeds
        printWords "forbidden" wordPool.Forbiddens

        let printPatterns name (patterns : IHS<Pattern>) =
            printfn "%d %s patterns: " patterns.Count name
            for pattern in patterns do
                printfn "    %O" pattern
            printfn ""

        printPatterns "seed" patternPool.Seeds
        printPatterns "forbidden" patternPool.Forbiddens

        printfn "%s" param.Text

    let printTaskFull printsLogs task (seedPatternScores, seedWordScores) =
        printTask (task.Name, task.WordPool, task.PatternPool, task.Param)

        let cache = task.PatternCache

        let doPrintLogs (logs : IHS<Log>) =
            if printsLogs then
                printfn " at %d log events:" logs.Count
                for log in logs do
                    printfn "        %s" log
            else
                printfn ""
        
        let extractedSeeds = HashSet ()
        let extractedForbiddens = HashSet ()
        let extractedOthers = HashSet ()
        printfn "\nPATTERNS =======> WORDS:"
        for (pattern, score) in seedPatternScores do
            let wordMap = cache.Get pattern
            let words = wordMap.Keys |> Array.ofSeq
            let seeds      = words |> Array.filter task.WordPool.Seeds.Contains
            let forbiddens = words |> Array.filter task.WordPool.Forbiddens.Contains
            let others     = words |> Array.filter (fun w -> not (task.WordPool.Seeds.Contains w) &&
                                                             not (task.WordPool.Forbiddens.Contains w))
            extractedSeeds.UnionWith seeds
            extractedForbiddens.UnionWith forbiddens
            extractedOthers.UnionWith others
            printfn "Pattern %O [score=%.2f] extracts %d words (%d seeds, %d forbiddens, and %d others):"
                        pattern score words.Length seeds.Length forbiddens.Length others.Length
            for (ws, label) in [(seeds, "SEED"); (forbiddens, "FORBIDDEN"); (others, "OTHER")] do
                for word in ws do
                    printf "    Word %s [%s]" word label
                    doPrintLogs (wordMap.Get word)
        printfn "\nTHE %d SEED PATTERNS EXTRACT TOTALLY %d WORDS (%d SEEDS, %d FORBIDDENS, AND %d OTHERS):"
                    task.PatternPool.Seeds.Count
                    (extractedSeeds.Count + extractedForbiddens.Count + extractedOthers.Count)
                    extractedSeeds.Count extractedForbiddens.Count extractedOthers.Count
        for (ws, label) in [(extractedSeeds, "SEEDS"); (extractedForbiddens, "FORBIDDENS");
                                (extractedOthers, "OTHERS")] do
            printfn "%d %s: %s" ws.Count label (String.concat ", " ws)

        printfn "\nWORDS =======> PATTERNS:"
        for (word, score) in seedWordScores do
            let patterns = List ()
            cache.Iter (fun pattern wordMap ->
                match wordMap.TryGet word with
                | Some logs -> patterns.Add (pattern, logs)
                | None -> ())

            printfn "Word %s [score=%.2f] is extracted by %d patterns:" word score patterns.Count
            for (pattern, logs) in patterns do
                printf "    Pattern %O " pattern
                doPrintLogs logs
