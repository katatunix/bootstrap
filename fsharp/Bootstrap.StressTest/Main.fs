namespace Bootstrap.StressTest

module Main =

    [<EntryPoint>]
    let main args =
        let testOption =
            match args with
            | [| "PerformanceNothing" |] ->
                Some TO_PerformanceNothing
            | [| "CachingOnly" |] ->
                Some TO_CachingOnly
            | [| "QualityNothing" |] ->
                Some TO_QualityNothing
            | [| "DynamicOnly" |] ->
                Some TO_DynamicOnly
            | [| "Full" |] ->
                Some TO_Full
            | _ ->
                None
        match testOption with
        | Some o ->
            printfn "%A" o
            (TestSuite o).runAllTests ()
        | None ->
            printfn """Usage: Bootstrap.StressTest PerformanceNothing | CachingOnly | 
                        QualityNothing | DynamicOnly | Full"""
        0
