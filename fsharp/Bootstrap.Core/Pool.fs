namespace Bootstrap.Core

open System.Collections.Generic
open NghiaBui.Common

type 'T Pool when 'T : equality () =
    let seeds = HashSet<'T> ()
    let forbiddens = HashSet<'T> ()
    let iSeeds = IHS seeds
    let iForbiddens = IHS forbiddens

    member this.Seeds = iSeeds
    member this.Forbiddens = iForbiddens

    member this.AddSeeds (xs : seq<'T>) =
        seeds.UnionWith xs
        for x in xs do forbiddens.Remove x |> ignore
        this

    member this.AddForbiddens (xs : seq<'T>) =
        forbiddens.UnionWith xs
        for x in xs do seeds.Remove x |> ignore
        this

    member this.Remove (xs : seq<'T>) =
        for x in xs do
            seeds.Remove x |> ignore
            forbiddens.Remove x |> ignore
        this

    member this.Clear () =
        seeds.Clear ()
        forbiddens.Clear ()
        this

    member this.isNewAndValid x =
        not (seeds.Contains x) && not (forbiddens.Contains x)
