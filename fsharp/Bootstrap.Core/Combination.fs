namespace Bootstrap.Core

open System.Threading.Tasks
open System.Collections.Generic
open NghiaBui.Common

[<AutoOpen>]
module Combination =

    let makeParallel (combine : Log -> 'Ele -> IHS<'Fri>) =
        fun (logs : seq<Log>) element ->
            let result = FriendMap<'Fri> ()
            Parallel.ForEach (logs, (fun log ->
                let friends = combine log element
                lock result (fun _ -> for friend in friends do result.Add friend log)))
            |> ignore
            result

    let makeSequential (combine : Log -> 'Ele -> IHS<'Fri>) =
        fun (logs : seq<Log>) element ->
            let result = FriendMap<'Fri> ()
            for log in logs do
                let friends = combine log element
                for friend in friends do result.Add friend log
            result

    let useCache    (cache : Cache<'Ele, 'Fri>)
                    (combine : seq<Log> -> 'Ele -> FriendMap<'Fri>) =
        fun logs element ->
            match cache.TryGet element with
            | Some friends -> friends
            | None ->   let friends = combine logs element
                        cache.Add element friends
                        friends

    let slim logs (combine : seq<Log> -> 'Ele -> FriendMap<'Fri>) =
        fun element -> (combine logs element).Keys

    let massive (combine : 'Ele -> IHS<'Fri>) =
        fun (elements : seq<'Ele>) ->
            let result = HashSet ()
            for e in elements do
                result.UnionWith (combine e)
            result |> IHS

    let massiveTimeout (combine : 'Ele -> IHS<'Fri>) timeoutSec =
        fun (elements : seq<'Ele>) ->
            let sw = StopWatch ()
            let res, pending =
                elements
                |> foldWithEarlyExit
                    (fun _ ->
                        match timeoutSec with   | None -> false
                                                | Some sec -> sw.ElapseSec >= sec)
                    (fun (result : HashSet<'Fri>, pending : HashSet<'Ele>) e ->
                        result.UnionWith (combine e)
                        pending.Remove e |> ignore
                        result, pending)
                    (HashSet (), HashSet (elements))
            res |> IHS, pending |> IHS
