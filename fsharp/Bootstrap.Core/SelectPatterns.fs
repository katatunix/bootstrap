namespace Bootstrap.Core

open NghiaBui.Common

[<AutoOpen>]
module SelectPatterns =

    let private scoreWordSet (seedWords : IHS<Word>) (words : IHS<Word>) =
        let frequency = countIntersect seedWords words
        if frequency = 0 then 0.0
        else
            let frequency = float frequency
            let reliability = frequency / (float words.Count)
            reliability * log2 frequency
   
    let private scorePattern (extractWords : Pattern -> IHS<Word>) seedWords pattern =
        pattern |> extractWords |> scoreWordSet seedWords

    let private scorePatternBlock (extractWords : Pattern -> IHS<Word>) seedWords (patterns : seq<Pattern>) =
        patterns |> massive extractWords |> scoreWordSet seedWords

    let private isSignificant (extractWords : Pattern -> IHS<Word>) (wordPool : Word Pool) =
        extractWords >> Seq.exists wordPool.isNewAndValid

    let sortPatterns extractWords seedWords =
        Seq.map (fun pattern -> pattern, pattern |> scorePattern extractWords seedWords)
        >> Array.ofSeq
        >> Array.sortByDescending snd

    let private abstractSelectPatterns
                                minLenFun
                                extractWords
                                seedWords
                                threshold
                                oldPatterns
                                newPatterns =
        let sort = sortPatterns extractWords seedWords
        let oldScores = sort oldPatterns
        let newScores = sort newPatterns
        let scores = Array.append oldScores newScores
        let patterns = scores |> Array.map fst
        let overallScores = patterns
                                |> Array.mapi (fun i pattern ->
                                    let blockScore = patterns
                                                        |> Seq.take (i + 1)
                                                        |> scorePatternBlock extractWords seedWords
                                    pattern, blockScore)
        let minLen = minLenFun patterns
        let fromIndex = max (minLen - 1) 0
        let toIndex =
            newScores
            |> Array.tryFindIndex (fun (_, score) -> score < threshold)
            |> function
                | Some i -> i - 1
                | None -> newScores.Length - 1
            |> (+) oldScores.Length

        let len =
            seq { fromIndex .. toIndex }
            |> Seq.map (fun i -> i, snd overallScores.[i])
            |> findMax (fun (i, score1) (j, score2) ->
                if abs (score1 - score2) < 0.01 then i > j
                else score1 < score2)
            |> function
                | None -> 0
                | Some (i, _) -> i + 1

        let resultLen, overallScore =
            if len = 0 then 0, 0.0
            else
                let s = snd overallScores.[len - 1]
                if s < threshold then 0, 0.0
                else len, s
        scores, resultLen, overallScore

    let selectBestPatterns  extractWords
                            (wordPool : Word Pool)
                            threshold
                            iter
                            oldPatterns 
                            (newPatterns : IHS<Pattern>) =
        let scores, resultLen, overallScore =
            abstractSelectPatterns
                (fun patterns ->
                    patterns
                        |> Array.tryFindIndex (isSignificant extractWords wordPool)
                        |> Option.defaultValue patterns.Length
                        |> (+) 1)
                extractWords
                wordPool.Seeds
                threshold
                oldPatterns
                newPatterns
        let selected, unselected =
            scores
            |> Array.map (fun (pattern, score) ->
                pattern,
                score,
                pattern |> newPatterns.Contains,
                pattern |> isSignificant extractWords wordPool)
            |> Array.splitAt resultLen
        selected, overallScore, unselected

    let prunePatterns   extractWords
                        seedWords
                        threshold
                        (oldPatterns : IHS<Pattern>)
                        newPatterns =
        let olen = oldPatterns.Count
        let scores, resultLen, overallScore =
            abstractSelectPatterns
                (fun _ -> olen)
                extractWords
                seedWords
                threshold
                oldPatterns
                newPatterns
        let len = if resultLen = 0 then olen else resultLen
        let olds = scores |> Array.take olen
        let selected = Array.sub scores olen (len - olen)
        let unselected = Array.sub scores len (scores.Length - len)
        selected, overallScore, unselected, olds

    //====================================================================================================

    let riloffSelectBestPatterns    extractWords
                                    (wordPool : Word Pool)
                                    threshold
                                    iter
                                    oldPatterns
                                    (newPatterns : IHS<Pattern>) =
        let sort = sortPatterns extractWords wordPool.Seeds
        let scores = 
            Array.append (sort oldPatterns) (sort newPatterns)
            |> Array.map (fun (pattern, score) ->
                pattern,
                score,
                pattern |> newPatterns.Contains,
                pattern |> isSignificant extractWords wordPool)
        let selected, unselected =
            scores
            |> Array.partition (fun (_, score, _, isSig) -> score >= threshold && isSig)
        let len = min (19 + iter) selected.Length
        let selected, tail = selected |> Array.splitAt len
        let unselected = Array.append tail unselected
        let overallScore =  if selected.Length = 0 then 0.0
                            else selected |> Array.averageBy (fun (_, score, _, _) -> score)
        selected, overallScore, unselected
