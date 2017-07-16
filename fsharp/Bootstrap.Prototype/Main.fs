namespace Bootstrap.Prototype

open NghiaBui.Common

module Main =

    let invalidParams () = printfn "Error: Invalid parameters"; 1

    [<EntryPoint>]
    let main args =
        match args with
        | [||] ->
            UC.printUsage ()

        | Array ("list", parameters) ->
            match parameters with
            | [||] -> UC.listAll ()
            | _ -> invalidParams ()

        //================================================================================================

        | Array ("crepo", parameters) ->
            match parameters with
            | [| name; logFile |] -> UCRepo.create name logFile
            | _ -> invalidParams ()

        | Array ("delrepo", parameters) ->
            match parameters with
            | [| Int repoId |] -> UCRepo.delete repoId
            | _ -> invalidParams ()

        | Array ("renrepo", parameters) ->
            match parameters with
            | [| Int repoId; newName |] -> UCRepo.rename repoId newName
            | _ -> invalidParams ()

        | Array ("viewrepo", parameters) ->
            match parameters with
            | [| Int repoId |] -> UCRepo.view repoId
            | _ -> invalidParams ()

        | Array ("al", parameters) ->
            match parameters with
            | [| Int repoId; logFile |] -> UCRepo.addLogs repoId logFile
            | _ -> invalidParams ()

        | Array ("rl", parameters) ->
            match parameters with
            | [| Int repoId; logFile |] -> UCRepo.removeLogs repoId logFile
            | _ -> invalidParams ()

        | Array ("cl", parameters) ->
            match parameters with
            | [| Int repoId |] -> UCRepo.clearLogs repoId
            | _ -> invalidParams ()

        //================================================================================================

        | Array ("ctask", parameters) ->
            match parameters with
            | [| Int repoId; taskName |] -> UCTask.create repoId taskName
            | _ -> invalidParams ()

        | Array ("deltask", parameters) ->
            match parameters with
            | [| Int taskId |] -> UCTask.delete taskId
            | _ -> invalidParams ()

        | Array ("rentask", parameters) ->
            match parameters with
            | [| Int taskId; newName |] -> UCTask.rename taskId newName
            | _ -> invalidParams ()

        | Array ("viewtask", parameters) ->
            match parameters with
            | [| Int taskId |]                  -> UCTask.view taskId
            | [| Int taskId; "full" |]          -> UCTask.viewFull taskId false
            | [| Int taskId; "full"; "logs" |]  -> UCTask.viewFull taskId true
            | _ -> invalidParams ()

        | Array ("aw", parameters) ->
            match parameters with
            | Array (Int taskId, words) when words.Length > 0 ->
                UCTask.addWords taskId words
            | _ -> invalidParams ()

        | Array ("fw", parameters) ->
            match parameters with
            | Array (Int taskId, words) when words.Length > 0 ->
                UCTask.forbidWords taskId words
            | _ -> invalidParams ()

        | Array ("rw", parameters) ->
            match parameters with
            | Array (Int taskId, words) when words.Length > 0 ->
                UCTask.removeWords taskId words
            | _ -> invalidParams ()

        | Array ("cw", parameters) ->
            match parameters with
            | [| Int taskId |] ->
                UCTask.clearWords taskId
            | _ -> invalidParams ()

        | Array ("ap", parameters) ->
            match parameters with
            | Array (Int taskId, patterns) when patterns.Length > 0 ->
                UCTask.addPatterns taskId patterns
            | _ -> invalidParams ()

        | Array ("fp", parameters) ->
            match parameters with
            | Array (Int taskId, patterns) when patterns.Length > 0 ->
                UCTask.forbidPatterns taskId patterns
            | _ -> invalidParams ()

        | Array ("rp", parameters) ->
            match parameters with
            | Array (Int taskId, patterns) when patterns.Length > 0 ->
                UCTask.removePatterns taskId patterns
            | _ -> invalidParams ()

        | Array ("cp", parameters) ->
            match parameters with
            | [| Int taskId |] ->
                UCTask.clearPatterns taskId
            | _ -> invalidParams ()

        | Array ("up", parameters) ->
            match parameters with
            | [|    Int taskId
                    Int iterNum
                    Float threshold
                    Int bestWordNum
                    Int timeoutSec |] ->
                UCTask.updateParam taskId iterNum threshold bestWordNum timeoutSec
            | _ -> invalidParams ()

        | Array ("run", parameters) ->
            match parameters with
            | [| Int taskId |] -> UCTask.run taskId
            | _ -> invalidParams ()

        | _ -> printfn "Error: Unknown command"; 1
