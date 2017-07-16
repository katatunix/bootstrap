namespace Bootstrap.Core

open NghiaBui.Common

type ConsoleProgress () =

    let MAX = 100
    let globalSw = StopWatch ()
    let sw = StopWatch ()

    let printPatterns patterns =
        for p in patterns do
            printfn "    %O" p

    let printPatternStatus statusArray =
        for (pattern, score, isNew, isSignificant) in statusArray do
            let isNewStr = if isNew then "NEW" else "OLD"
            let isSignificantStr = if isSignificant then "SIGNIFICANT" else "DEPLETED"
            printfn "    %O [score=%.2f] [%s] [%s]" pattern score isNewStr isSignificantStr
    
    let printPatternScores scores =
        for (p, score) in scores do
            printfn "    %O [score=%.2f]" p score

    static member HrLine =
        printfn "======================================================================================"

    interface Progress with

        member this.BeginLearning numLogs numWords numPatterns param =
            printfn "LEARNING WITH %d LOG EVENTS, %d SEED WORDS, AND %d SEED PATTERNS"
                        numLogs numWords numPatterns
            printfn "%s" param.Text
            printfn ""
            globalSw.Reset ()
        
        member this.BeginIter iter =
            ConsoleProgress.HrLine
            printfn "ITERATION: %d\n" iter

        member this.BeginGenNewPatterns numWords =
            printf "Generating new patterns from %d words ... " numWords
            sw.Reset ()

        member this.EndGenNewPatterns patterns numWordsRemain =
            printfn "Time: %ds" sw.ElapseSec
            if numWordsRemain > 0 then
                printf "TIMEOUT!!! %d words left!!! " numWordsRemain
            printfn "%d new patterns:" patterns.Count
            printPatterns patterns
            printfn ""

        member this.BeginSelectBestPatterns num =
            printf "Selecting best patterns among %d patterns ... " num
            sw.Reset ()

        member this.EndSelectBestPatterns bests overallScore others =
            printfn "Time: %ds" sw.ElapseSec
            printf "%d best patterns" bests.Length
            if bests.Length > 0 then
                printfn " with overall score [%.2f]:" overallScore
            else
                printfn ":"
            printPatternStatus bests

            printfn "%d other (not selected) patterns:" others.Length
            printPatternStatus others

            printfn ""

        member this.BeginExtractNewWords () =
            printf "Extracting new words ... "
            sw.Reset ()

        member this.EndExtractNewWords words =
            printfn "Time: %ds" sw.ElapseSec
            let num = words.Count
            printf "%d new words: %s" num (String.concat ", " (words |> Seq.truncate MAX))
            if num > MAX then
                printf " (and %d more words)" (num - MAX)
            printfn "\n"

        member this.BeginSelectBestWords () =
            printf "Selecting best words ... "
            sw.Reset ()

        member this.EndSelectBestWords words =
            printfn "Time: %ds" sw.ElapseSec
            let num = words.Length
            printf "%d best words: %s" num
                    (String.concat ", "
                        (words  |> Seq.truncate MAX
                                |> Seq.map (fun (w, score) -> sprintf "%s [score=%.2f]" w score)))
            if num > MAX then
                printf " (and %d more words)" (num - MAX)
            printfn "\n"
        
        member this.EndBootstrap reason words patterns =
            ConsoleProgress.HrLine
            printfn "End bootstrapping because of %A" reason
            printfn ""

            let numWords = words.Count
            printf "Learned %d new words: %s" numWords (String.concat ", " (words |> Seq.truncate MAX))
            if numWords > MAX then
                printf " (and %d more words)" (numWords - MAX)
            printfn "\n"

            printfn "Collected %d new patterns:" patterns.Count
            printPatterns patterns
            printfn ""

        member this.BeginPruning numOldPatterns numNewPatterns =
            ConsoleProgress.HrLine
            printf "Pruning with %d old patterns and %d new patterns ... " numOldPatterns numNewPatterns
            sw.Reset ()

        member this.EndPruning selected overallScore unselected olds =
            printfn "Time: %ds" sw.ElapseSec
            printfn ""

            printf "Finally learned %d best new patterns" selected.Length
            if selected.Length > 0 then
                printfn " with overall score (in conjunction with old patterns) [%.2f]:" overallScore
            else
                printfn ":"
            printPatternScores selected

            printfn "Scores of %d old patterns:" olds.Length
            printPatternScores olds

            printfn "Scores of %d other (new) patterns:" unselected.Length
            printPatternScores unselected

            printfn ""

        member this.BeginFinalExtract () =
            ConsoleProgress.HrLine
            printf "Extracting words from the final patterns ... "
            sw.Reset ()

        member this.EndLearning seedWords forbiddenWords otherWords =
            printfn "Time: %ds" sw.ElapseSec

            printfn "The new patterns extract totally %d words (%d seeds, %d forbiddens, and %d others):"
                    (seedWords.Count + forbiddenWords.Count + otherWords.Count)
                    seedWords.Count forbiddenWords.Count otherWords.Count
            for (words, label) in [ (seedWords, "SEED")
                                    (forbiddenWords, "FORBIDDEN")
                                    (otherWords, "OTHER") ] do
                for word in words do
                    printfn "    Word %s [%s]" word label

            printfn ""
            printfn "LEARNING TIME: %ds" globalSw.ElapseSec
