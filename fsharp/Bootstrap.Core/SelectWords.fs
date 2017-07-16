namespace Bootstrap.Core

open NghiaBui.Common

[<AutoOpen>]
module SelectWords =

    let private scoreWord   (refPatterns : seq<Pattern>)
                            (extractWordsAndFreq : Pattern -> IHS<Word> * int)
                            (word : Word) =
        let freqs =
            refPatterns
            |> Seq.choose (fun pattern -> 
                let words, frequency = extractWordsAndFreq pattern
                if words.Contains word then
                    log2 (1.0 + float frequency) |> Some
                else
                    None)
            |> Array.ofSeq
        if freqs.Length = 0 then 0.0 else freqs |> Array.average

    let selectBestWords num (refPatterns : seq<Pattern>) extractWords (seedWords : IHS<Word>) words =
        let extractWordsAndFreq = memoize (fun pattern ->
            let words = extractWords pattern
            let frequency = countIntersect words seedWords
            words, frequency)
        words
        |> Seq.map (fun word -> word, word |> scoreWord refPatterns extractWordsAndFreq)
        |> Array.ofSeq
        |> Array.sortByDescending snd
        |> Array.truncate num

    let sortWords refPatterns extractWords seedWords (words : IHS<Word>) =
        selectBestWords words.Count refPatterns extractWords seedWords words
