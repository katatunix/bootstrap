namespace Bootstrap.Prototype

open System.Collections.Generic
open NghiaBui.Common
open Bootstrap.Core

[<AutoOpen>]
module DomainFuns =

    module Repo =
        let emptyLogs () = HashSet<Log> ()

        let empty name = { Name = name; Logs = emptyLogs () }

        let addLogs newLogs repo =
            for log in newLogs do
                repo.Logs.Add log |> ignore
            repo

        let removeLogs oldLogs repo =
            for log in oldLogs do
                repo.Logs.Remove log |> ignore
            repo

    module Task =
        let create name =
            {   Name         = name
                WordPool     = Pool<Word> ()
                PatternPool  = Pool<Pattern> ()
                WordCache    = Cache ()
                PatternCache = Cache ()
                Param        = {    IterNum = 10;
                                    Threshold = 0.7;
                                    BestWordNum = 5;
                                    TimeoutSec = Some 60 } }

        let run task repo =
            learn   (repo.Logs |> IHS)
                    task.WordPool
                    task.PatternPool
                    task.Param
                    (ConsoleProgress ())
                    task.WordCache
                    task.PatternCache
            task

        let resetDirty task =
            task.WordCache.ResetDirty ()
            task.PatternCache.ResetDirty ()
            task

        let updateCachesWithNewLogs (logs : seq<Log>) task =
            let genPatterns = (genPatterns |> makeParallel) logs
            for word in task.WordCache.Keys do
                task.WordCache.Add word (genPatterns word)

            let extractWords = (extractWords |> makeParallel) logs
            for pattern in task.PatternCache.Keys do
                task.PatternCache.Add pattern (extractWords pattern)

            task

        let updateCachesWithRemovedLogs (logs : seq<Log>) task =
            task.PatternCache.RemoveLogs logs
            task.WordCache.RemoveLogs logs
            task

        let updateCachesWithClearLogs task =
            task.PatternCache.ClearLogs ()
            task.WordCache.ClearLogs ()
            task

        let updatePatternCacheAndCalculateSeedScores task (repo : Repo) =
            let extractWords = extractWords |> makeParallel
                                            |> useCache task.PatternCache
                                            |> slim repo.Logs
            task.PatternCache.Retain task.PatternPool.Seeds

            let patternScores =
                task.PatternPool.Seeds
                |> sortPatterns extractWords task.WordPool.Seeds

            let wordScores =
                task.WordPool.Seeds
                |> sortWords task.PatternPool.Seeds extractWords task.WordPool.Seeds

            patternScores, wordScores
