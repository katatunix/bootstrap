namespace Bootstrap.Core

open System.Collections.Generic
open System.Text
open NghiaBui.Common

[<AutoOpen>]
module GenPatterns =

    let inline countChar key (s : string) fromIndex toIndex =
        let mutable count = 0
        for i = fromIndex to toIndex do
            if s.[i] = key then count <- count + 1
        count

    let inline isLetter ch =
        ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch = '_'

    let inline isDigit ch =
        '0' <= ch && ch <= '9'

    let inline isLetterOrDigit ch =
        isLetter ch || isDigit ch

    let inline isSpaceOrTab ch =
        ch = ' ' || ch = '\t'

    let esces = "\\.[]{}()*+-?^$|"
    let inline isEscapeChar (ch : char) =
        esces.IndexOf ch <> -1

    let inline genLiteral (s : string) fromIndex toIndex =
        let sb = StringBuilder()
        for i = fromIndex to toIndex do
            let ch = s.[i]
            if isEscapeChar ch then sb.Append @"\" |> ignore
            sb.Append ch |> ignore
        sb.ToString ()

    let inline genLiteralChar ch =
        if isEscapeChar ch then @"\" + (string ch) else (string ch)

    let inline areValidWordBounds (log : string) left right =
        ( left < 0 || not (isLetterOrDigit log.[left]) ) &&
        ( right >= log.Length || not (isLetterOrDigit log.[right]) )

    type GenState = Start | Literal | Letter | Digit
    let genLettersAndDigitsOnly (s : string) fromIndex toIndex =
        let sb = StringBuilder ()
        let mutable state = Start
        for i = fromIndex to toIndex do
            let ch = s.[i]
            if isDigit ch then
                if state <> Digit then sb.Append @"\d+" |> ignore; state <- Digit
            elif isLetter ch then
                if state <> Letter then sb.Append @"\w+" |> ignore; state <- Letter
            else
                sb.Append (genLiteralChar ch) |> ignore
                state <- Literal
        sb.ToString ()

    let inline isTagDelim (ch : char) =
        ch = '=' || ch = ':'

    type TagState =
        | WaitingForDelim
        | WaitingForTag
        | InTag
        | Bad
        | Found of int

    let findTagStartIndex (log : string) endIndex =
        let rec loop state i =
            if i < 0 then state
            else
                let ch = log.[i]
                match state with
                | WaitingForDelim ->
                    let state = if isSpaceOrTab ch then state
                                elif isTagDelim ch then WaitingForTag
                                else Bad
                    loop state (i - 1)
                | WaitingForTag ->
                    let state = if isSpaceOrTab ch then state
                                elif isLetterOrDigit ch then InTag
                                else Bad
                    loop state (i - 1)
                | InTag ->
                    let state = if isLetterOrDigit ch then state
                                elif isSpaceOrTab ch then Found (i + 1)
                                else Bad
                    loop state (i - 1)
                | Bad | Found _ ->
                    state
        
        let state = loop WaitingForDelim endIndex

        let inline checkLetter i = if isLetter log.[i] then i else -1

        match state with
        | Bad -> -1
        | Found i -> checkLetter i
        | InTag -> checkLetter 0
        | _ -> -1

    let puncts = "\t()[]{}<>\\/*+-^$@#%=:,;!?"
    let inline isPunct (ch : char) =
        puncts.IndexOf ch <> -1

    let inline findBack (s : string) from pred =
        let mutable i = from
        while i >= 0 && not (pred s.[i]) do
            i <- i - 1
        i

    let inline genPrefix (s : string) toIndex =
        if toIndex = -1 then ""
        else
            let punct = s.[toIndex]
            let count = countChar punct s 0 toIndex
            let lit = genLiteralChar punct
            (StringBuilder ())
                .Append("(?:[^").Append(lit).Append("]*").Append(lit).Append("){").Append(count).Append("}")
                .ToString()

    let inline genPostPrefix log fromIndex toIndex =
        genLettersAndDigitsOnly log fromIndex toIndex

    let inline genTag log fromIndex toIndex =
        genLiteral log fromIndex toIndex

    let inline genWord log fromIndex toIndex =
        genLettersAndDigitsOnly log fromIndex toIndex
        
    let inline genSuffix suffix =
        match suffix with None -> "$" | Some ch -> genLiteralChar ch

    let inline genAtMatching start (word : string) log =
        let finish = start + word.Length
        if not (areValidWordBounds log (start - 1) finish) then
            None
        else
            let t = findTagStartIndex log (start - 1)
            let tagIndex = if t = -1 then start else t
            let punctIndex = findBack log (tagIndex - 1) isPunct

            (StringBuilder ())
                .Append("^")
                .Append(genPrefix       log punctIndex)
                .Append(genPostPrefix   log (punctIndex + 1) (tagIndex - 1))
                .Append(genTag          log tagIndex (start - 1))
                .Append("(")
                .Append(genWord         log start (finish - 1))
                .Append(")")
                .Append(genSuffix (if finish < log.Length then Some log.[finish] else None))
                .ToString() 
            |> Some

    let genPatterns (log : Log) (word : Word) =
        let rec loop (result : HashSet<Pattern>) (from : int) =
            let index = log.IndexOf (word, from)
            if index = -1 then result
            else
                match genAtMatching index word log with
                | Some regex -> regex |> Pattern |> result.Add |> ignore
                | None -> ()
                loop result (index + 1)
        
        loop (HashSet ()) 0 |> IHS
