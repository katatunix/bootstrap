namespace Bootstrap.Prototype

open Result

open NghiaBui.Common
open NghiaBui.Common.Rop
open Bootstrap.Core

module UCTask =

    let create repoId taskName =
        repoId
        |> UCRepo.checkRepoExist
        |> map (fun _ -> Task.create taskName)
        |> bind (liftExn (db.InsertTask repoId))
        |> map UI.printCreateTaskSuccess
        |> UI.printIfErrorAndMakeExitCode

    let private checkTaskExist (id : int) =
        id
        |> liftExn db.CheckTaskExist
        |> bind (fun existed -> if existed then Ok id else Error "The task does not exist")

    let delete id =
        id
        |> checkTaskExist
        |> bind (liftExn (fun _ -> db.DeleteTask id))
        |> UI.printIfErrorAndMakeExitCode

    let rename id newName =
        id
        |> checkTaskExist
        |> bind (liftExn (db.SaveTaskName newName))
        |> UI.printIfErrorAndMakeExitCode

    let updateParam id iterNum threshold bestWordNum timeoutSec =
        let newParam = {    IterNum         = iterNum
                            Threshold       = threshold
                            BestWordNum     = bestWordNum
                            TimeoutSec      = if timeoutSec = 0 then None else Some timeoutSec }
        id
        |> checkTaskExist
        |> bind (liftExn (db.SaveTaskParam newParam))
        |> UI.printIfErrorAndMakeExitCode

    let private actionWords id (action : Word Pool -> Word Pool) =
        id
        |> checkTaskExist
        |> bind (liftExn db.LoadWordPool)
        |> map action
        |> bind (liftExn (db.SaveWordPool id))
        |> UI.printIfErrorAndMakeExitCode
    let addWords    id words = actionWords id (fun pool -> pool.AddSeeds words)
    let forbidWords id words = actionWords id (fun pool -> pool.AddForbiddens words)
    let removeWords id words = actionWords id (fun pool -> pool.Remove words)
    let clearWords  id       = actionWords id (fun pool -> pool.Clear ())

    let private actionPatterns id (action : Pattern Pool -> Pattern Pool) =
        id
        |> checkTaskExist
        |> bind (liftExn db.LoadPatternPool)
        |> map action
        |> bind (liftExn (db.SavePatternPool id))
        |> UI.printIfErrorAndMakeExitCode
    let mapPat patterns = patterns |> Seq.map Pattern
    let addPatterns    id patterns = actionPatterns id (fun pool -> pool.AddSeeds (mapPat patterns))
    let forbidPatterns id patterns = actionPatterns id (fun pool -> pool.AddForbiddens (mapPat patterns))
    let removePatterns id patterns = actionPatterns id (fun pool -> pool.Remove (mapPat patterns))
    let clearPatterns  id          = actionPatterns id (fun pool -> pool.Clear ())

    let private loadCleanTask id = let task = db.LoadTask id in Task.resetDirty task

    let private saveIfDirty id task =
        UI.printSavingTask id

        db.SaveWordPool id task.WordPool
        db.SavePatternPool id task.PatternPool
        if task.WordCache.IsDirty then
            db.SaveWordCache id task.WordCache
        if task.PatternCache.IsDirty then
            db.SavePatternCache id task.PatternCache

    let run id =
        id
        |> checkTaskExist
        |> map UI.printLoadingTask
        |> bind (liftExn loadCleanTask)
        |> bind (fun task ->
            id
            |> liftExn db.LoadRepoIdOfTask
            |> map UI.printLoadingRepo
            |> bind (liftExn db.LoadRepo)
            |> map (Task.run task)
            |> bind (liftExn (saveIfDirty id)))
        |> UI.printIfErrorAndMakeExitCode

    let view id =
        id
        |> checkTaskExist
        |> bind (liftExn (fun _ ->
            db.LoadTaskName     id,
            db.LoadWordPool     id,
            db.LoadPatternPool  id,
            db.LoadTaskParam    id))
        |> map UI.printTask
        |> UI.printIfErrorAndMakeExitCode

    let viewFull id viewsLogs =
        id
        |> checkTaskExist
        |> map UI.printLoadingTask
        |> bind (liftExn loadCleanTask)
        |> bind (fun task ->
            id
            |> liftExn db.LoadRepoIdOfTask
            |> map UI.printLoadingRepo
            |> bind (liftExn db.LoadRepo)
            |> map (fun repo -> UI.printUpdatingTask id; repo)
            |> map (Task.updatePatternCacheAndCalculateSeedScores task)
            |> map (UI.printTaskFull viewsLogs task)
            |> bind (liftExn (fun _ ->
                if task.PatternCache.IsDirty then
                    UI.printSavingTask id
                    db.SavePatternCache id task.PatternCache)))
        |> UI.printIfErrorAndMakeExitCode
