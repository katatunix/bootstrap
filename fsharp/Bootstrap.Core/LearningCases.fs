namespace Bootstrap.Core

open NghiaBui.Common

[<AutoOpen>]
module LearningCases =

    type PerformanceCase =
        | PerformanceNothing
        | CachingOnly of Cache<Pattern, Word> * Cache<Word, Pattern>
        | CachingAndParallel of Cache<Pattern, Word> * Cache<Word, Pattern>
    
    type QualityCase =
        | QualityNothing
        | DynamicOnly
        | DynamicAndPruning

    let private sayBegin    (prog : Progress)
                            (logs : IHS<Log>)
                            (wordPool : Word Pool)
                            (patternPool : Pattern Pool)
                            (param : Param) =
        prog.BeginLearning logs.Count wordPool.Seeds.Count patternPool.Seeds.Count param

    let private sayEnd      (prog : Progress)
                            (newPatterns : IHS<Pattern>)
                            extractWords
                            (wordPool : Word Pool) =
        prog.BeginFinalExtract ()
        let extractedWords = newPatterns |> (massive extractWords)

        prog.EndLearning    (extractedWords |> IHS.filter wordPool.Seeds.Contains)
                            (extractedWords |> IHS.filter wordPool.Forbiddens.Contains)
                            (extractedWords |> IHS.filter (fun w -> not (wordPool.Seeds.Contains w) &&
                                                                    not (wordPool.Forbiddens.Contains w)))

    let learnWith performanceCase qualityCase logs wordPool patternPool param prog =
        sayBegin prog logs wordPool patternPool param

        let extractWords =
            extractWords
            |>  match performanceCase with
                | PerformanceNothing ->
                    makeSequential
                | CachingOnly (pCache, _) ->
                    makeSequential >> useCache pCache
                | CachingAndParallel (pCache, _) ->
                    makeParallel >> useCache pCache
            |> slim logs

        let genPatterns =
            genPatterns

            |>  match performanceCase with
                | PerformanceNothing ->
                    makeSequential
                | CachingOnly (_, wCache) ->
                    makeSequential >> (useCache wCache)
                | CachingAndParallel (_, wCache) ->
                    makeParallel >> (useCache wCache)
            |> slim logs

        let selectBestPatterns =
            match qualityCase with
            | QualityNothing -> riloffSelectBestPatterns
            | DynamicOnly
            | DynamicAndPruning -> selectBestPatterns

        let newPatterns =
            bootstrap selectBestPatterns wordPool patternPool param prog extractWords genPatterns

        let newPatterns =
            match qualityCase with
            | QualityNothing | DynamicOnly ->
                newPatterns
            | DynamicAndPruning ->
                newPatterns |> prune prog param.Threshold extractWords wordPool.Seeds patternPool.Seeds

        patternPool.AddSeeds newPatterns |> ignore

        match performanceCase with
        | CachingOnly (pCache, wCache)
        | CachingAndParallel (pCache, wCache) ->
            wCache.Retain wordPool.Seeds
            pCache.Retain patternPool.Seeds
        | _ -> ()

        sayEnd prog newPatterns extractWords wordPool

    let learn logs wordPool patternPool param prog wordCache patternCache =
        learnWith
            (CachingAndParallel (patternCache, wordCache))
            DynamicAndPruning
            logs wordPool patternPool param prog
