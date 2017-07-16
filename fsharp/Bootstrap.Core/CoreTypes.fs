namespace Bootstrap.Core

open System.Text.RegularExpressions

[<AutoOpen>]
module CoreTypes =

    type Word = string

    type Log = string

    type Pattern (content : string) =
        let mutable compiled = null

        member this.Match log =
            lock this (fun _ ->
                if isNull compiled then
                    compiled <- Regex (content, RegexOptions.Compiled))
            compiled.Match log

        override this.ToString () = content
        override this.GetHashCode () = content.GetHashCode ()
        override this.Equals o =
            match o with
            | :? string as s -> s = content
            | :? Pattern as p -> p.ToString () = content
            | _ -> false
