namespace Bootstrap.Prototype

open Result

open NghiaBui.Common
open NghiaBui.Common.Rop

[<AutoOpen>]
module UC =

    let db = DB "data"

    let printUsage () = UI.printUsage (); 0

    let listAll () =
        (fun _ ->
            let repos = db.LoadAllRepoIds ()
                        |> Array.map (fun repoId -> repoId, db.LoadRepoName repoId)
            let tasks = db.LoadAllTaskIds ()
                        |> Array.map (fun taskId ->
                            taskId, db.LoadTaskName taskId, db.LoadRepoIdOfTask taskId)
            repos, tasks)
        |> liftExn <| ()
        |> map UI.printListAll
        |> UI.printIfErrorAndMakeExitCode
