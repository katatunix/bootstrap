namespace Bootstrap.Prototype

open Result
open System.IO

open NghiaBui.Common
open NghiaBui.Common.Rop
open Bootstrap.Core

module UCRepo =

    let create name logFile =
        logFile
        |> liftExn File.ReadAllLines
        |> map (fun logs -> Repo.empty name |> Repo.addLogs logs)
        |> bind (liftExn db.InsertRepo)
        |> map UI.printCreateRepoSuccess
        |> UI.printIfErrorAndMakeExitCode

    let checkRepoExist repoId =
        repoId
        |> liftExn db.CheckRepoExist
        |> bind (fun existed -> if existed then Ok () else Error "The repo does not exist")

    let private loadTaskIdsOfRepo repoId =
        liftExn db.LoadAllTaskIds ()
        |> bind (liftExn (Array.filter (fun taskId -> db.LoadRepoIdOfTask taskId = repoId)))

    let delete repoId =
        repoId
        |> checkRepoExist
        |> bind (fun _ -> loadTaskIdsOfRepo repoId)
        |> bind (liftExn (fun taskIds -> for taskId in taskIds do db.DeleteTask taskId))
        |> bind (liftExn (fun _ -> db.DeleteRepo repoId))
        |> UI.printIfErrorAndMakeExitCode

    let rename repoId newName =
        repoId
        |> checkRepoExist
        |> bind (liftExn (fun _ -> db.SaveRepoName repoId newName))
        |> UI.printIfErrorAndMakeExitCode

    let view repoId =
        repoId
        |> checkRepoExist
        |> bind (liftExn (fun _ -> db.LoadRepo repoId))
        |> map UI.printRepo
        |> UI.printIfErrorAndMakeExitCode

    let private saveCachesIfDirty taskId task =
        if task.WordCache.IsDirty then
            db.SaveWordCache taskId task.WordCache
        if task.PatternCache.IsDirty then
            db.SavePatternCache taskId task.PatternCache

    let private actionLogs actionRepo actionTask repoId logFile =
        repoId
        |> checkRepoExist
        |> bind (liftExn (fun _ -> File.ReadAllLines logFile))
        |> bind (fun logs ->
            repoId
            |> UI.printLoadingRepo
            |> liftExn db.LoadRepo
            |> (fun repo -> UI.printUpdatingRepo repoId; repo)
            |> map (actionRepo logs)
            |> map (fun repo -> UI.printSavingRepo repoId |> ignore; repo)
            |> bind (liftExn (db.SaveRepo repoId))
            |> bind (fun _ -> loadTaskIdsOfRepo repoId)
            |> bind (liftExn (Array.iter (fun taskId ->
                taskId
                |> UI.printLoadingTask
                |> db.LoadTask
                |> Task.resetDirty
                |> (fun task -> UI.printUpdatingTask taskId; task)
                |> actionTask logs
                |> (fun task -> UI.printSavingTask taskId; task)
                |> saveCachesIfDirty taskId))))
        |> UI.printIfErrorAndMakeExitCode

    let addLogs = actionLogs Repo.addLogs Task.updateCachesWithNewLogs

    let removeLogs = actionLogs Repo.removeLogs Task.updateCachesWithRemovedLogs

    let clearLogs repoId =
        repoId
        |> checkRepoExist
        |> map (fun _ -> UI.printClearingRepo repoId)
        |> bind (liftExn (fun _ -> db.SaveRepoLogs repoId (Repo.emptyLogs ())))
        |> bind (fun _ -> loadTaskIdsOfRepo repoId)
        |> bind (liftExn (Array.iter (fun taskId ->
            taskId
            |> UI.printLoadingTask
            |> db.LoadTask
            |> Task.resetDirty
            |> (fun task -> UI.printUpdatingTask taskId; task)
            |> Task.updateCachesWithClearLogs
            |> (fun task -> UI.printSavingTask taskId; task)
            |> saveCachesIfDirty taskId)))
        |> UI.printIfErrorAndMakeExitCode 
