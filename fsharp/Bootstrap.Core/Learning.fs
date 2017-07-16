namespace Bootstrap.Core

open System.Collections.Generic
open NghiaBui.Common

[<AutoOpen>]
module Learning =

    let bootstrap   selectBestPatterns
                    (wordPool : Word Pool)
                    (patternPool : Pattern Pool)
                    (param : Param)
                    (progress : Progress)
                    (extractWords : Pattern -> IHS<Word>)
                    (genPatterns  : Word -> IHS<Pattern>) =
        let extractWordsMassive = massive extractWords
        let genPatternsMassive = massiveTimeout genPatterns param.TimeoutSec
        let removeLowPatterns = Array.filter (fun (_, score) -> score >= param.Threshold)

        let allPatterns = HashSet (patternPool.Seeds)
        let allNewPatterns = HashSet ()
        let allNewWords = HashSet ()

        let rec iter count (wordsForGen : IHS<Word>) =
            progress.BeginIter count

            progress.BeginGenNewPatterns wordsForGen.Count
            let newPatterns, pendingWords = genPatternsMassive wordsForGen
            let newPatterns = newPatterns
                                |> IHS.filter (fun p -> patternPool.isNewAndValid p &&
                                                        not (allNewPatterns.Contains p))
            allNewPatterns.UnionWith newPatterns
            allPatterns.UnionWith newPatterns
            progress.EndGenNewPatterns newPatterns pendingWords.Count

            progress.BeginSelectBestPatterns allPatterns.Count
            let bestPatterns, overallScore, otherPatterns =
                selectBestPatterns  extractWords
                                    wordPool
                                    param.Threshold
                                    count
                                    patternPool.Seeds
                                    (IHS allNewPatterns)
            progress.EndSelectBestPatterns bestPatterns overallScore otherPatterns

            let bestPatterns = bestPatterns |> Array.map (fun (pattern, _, _, _) -> pattern)
            if bestPatterns.Length = 0 && pendingWords.IsEmpty then
                NoMoreBestPatterns
            else
                progress.BeginExtractNewWords ()
                let newWords = bestPatterns |> extractWordsMassive |> IHS.filter wordPool.isNewAndValid
                progress.EndExtractNewWords newWords

                progress.BeginSelectBestWords ()
                let bestWords = selectBestWords param.BestWordNum
                                                allPatterns
                                                extractWords
                                                wordPool.Seeds
                                                newWords
                progress.EndSelectBestWords bestWords
                let bestWords = bestWords |> Seq.map fst |> IHS

                wordPool.AddSeeds bestWords |> ignore
                allNewWords.UnionWith bestWords

                if count = param.IterNum then
                    EnoughIterations
                else
                    iter (count + 1) (pendingWords + bestWords)

        let reason = iter 1 wordPool.Seeds
        let allNewWords = IHS allNewWords
        let allNewPatterns = IHS allNewPatterns
        progress.EndBootstrap reason allNewWords allNewPatterns
        allNewPatterns

    let prune   (progress : Progress)
                threshold
                extractWords
                seedWords
                (oldPatterns : IHS<Pattern>)
                (newPatterns : IHS<Pattern>) =
        progress.BeginPruning oldPatterns.Count newPatterns.Count
        let selected, overallScore, unselected, olds = prunePatterns
                                                            extractWords
                                                            seedWords
                                                            threshold
                                                            oldPatterns
                                                            newPatterns
        progress.EndPruning selected overallScore unselected olds
        selected |> Array.map fst |> IHS
        