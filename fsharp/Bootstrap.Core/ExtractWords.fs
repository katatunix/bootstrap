namespace Bootstrap.Core

open System.Collections.Generic
open System.Text.RegularExpressions
open NghiaBui.Common

[<AutoOpen>]
module ExtractWords =

    let extractWords (log : Log) (pattern : Pattern) : IHS<Word> =
        let m = pattern.Match log
        let result = HashSet ()
        if m.Success then
            for i = 1 to m.Groups.Count - 1 do
                let w = m.Groups.[i].Value
                if w.Length <> 0 then result.Add w |> ignore
        result |> IHS
