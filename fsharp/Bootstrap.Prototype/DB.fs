namespace Bootstrap.Prototype

open System.IO
open System.Collections.Generic

open NghiaBui.Common
open Bootstrap.Core

type DB (dataFolder) =

    do Directory.CreateDirectory dataFolder |> ignore

    let loadAllIds fileName =
        dataFolder
        |> Directory.GetFiles
        |> Array.filter (fun file -> Path.GetFileNameWithoutExtension file = fileName)
        |> Array.map (fun file -> (Path.GetExtension file).[1..] |> int)

    let newId =
        loadAllIds
        >> (fun arr -> if arr.Length = 0 then 1 else (arr |> Array.max) + 1)

    let checkExist fileName (id : int) =
        let key = fileName + "." + (string id)
        dataFolder
        |> Directory.GetFiles
        |> Array.exists (fun file -> Path.GetFileName file = key)

    let newRepoId () = newId "Repo.Name"
    let newTaskId () = newId "Task.Name"

    let file name id = dataFolder + "/" + name + "." + (string id)

    let fileRepoName = file "Repo.Name"
    let fileRepoLogs = file "Repo.Logs"

    let fileTaskName            = file "Task.Name"
    let fileTaskRepoId          = file "Task.RepoId"
    let fileTaskWordPool        = file "Task.WordPool"
    let fileTaskPatternPool     = file "Task.PatternPool"
    let fileTaskWordCache       = file "Task.WordCache"
    let fileTaskPatternCache    = file "Task.PatternCache"
    let fileTaskParam           = file "Task.Param"

    //======================================================================================
    member this.CheckRepoExist id = checkExist "Repo.Name" id
    member this.LoadAllRepoIds () = loadAllIds "Repo.Name"
    member this.CheckTaskExist id = checkExist "Task.Name" id
    member this.LoadAllTaskIds () = loadAllIds "Task.Name"

    member this.InsertRepo repo =
        let id = newRepoId ()
        this.SaveRepo id repo
        id

    member this.SaveRepo repoId (repo : Repo) =
        repoId |> fileRepoName |> IO.writeValueToFile repo.Name
        repoId |> fileRepoLogs |> IO.writeValueToFile repo.Logs

    member this.DeleteRepo repoId =
        repoId |> fileRepoName |> File.Delete
        repoId |> fileRepoLogs |> File.Delete

    member this.LoadRepo id =
        let name = id |> fileRepoName |> IO.readValueFromFile
        let logs = id |> fileRepoLogs |> IO.readValueFromFile
        { Name = name; Logs = logs }

    member this.SaveRepoName repoId (newName : string) =
        repoId |> fileRepoName |> IO.writeValueToFile newName

    member this.LoadRepoName repoId =
        repoId |> fileRepoName |> IO.readValueFromFile

    member this.SaveRepoLogs repoId logs =
        repoId |> fileRepoLogs |> IO.writeValueToFile logs

    //======================================================================================
    member this.InsertTask (repoId : int) task =
        let taskId = newTaskId ()
        taskId |> fileTaskName          |> IO.writeValueToFile task.Name
        taskId |> fileTaskRepoId        |> IO.writeValueToFile repoId
        taskId |> fileTaskWordPool      |> IO.writeValueToFile task.WordPool
        taskId |> fileTaskPatternPool   |> IO.writeValueToFile task.PatternPool
        taskId |> fileTaskWordCache     |> IO.writeValueToFile task.WordCache
        taskId |> fileTaskPatternCache  |> IO.writeValueToFile task.PatternCache
        taskId |> fileTaskParam         |> IO.writeValueToFile task.Param
        taskId

    member this.DeleteTask taskId =
        taskId |> fileTaskName          |> File.Delete
        taskId |> fileTaskRepoId        |> File.Delete
        taskId |> fileTaskWordPool      |> File.Delete
        taskId |> fileTaskPatternPool   |> File.Delete
        taskId |> fileTaskWordCache     |> File.Delete
        taskId |> fileTaskPatternCache  |> File.Delete
        taskId |> fileTaskParam         |> File.Delete

    member this.LoadTask id =
        {   Name         = id |> fileTaskName         |> IO.readValueFromFile
            WordPool     = id |> fileTaskWordPool     |> IO.readValueFromFile
            PatternPool  = id |> fileTaskPatternPool  |> IO.readValueFromFile
            WordCache    = id |> fileTaskWordCache    |> IO.readValueFromFile
            PatternCache = id |> fileTaskPatternCache |> IO.readValueFromFile
            Param        = id |> fileTaskParam        |> IO.readValueFromFile }

    member this.SaveTaskName (newName : string) id =
        id |> fileTaskName |> IO.writeValueToFile newName
    member this.LoadTaskName id : string =
        id |> fileTaskName |> IO.readValueFromFile

    member this.SaveTaskParam (newParam : Param) id =
        id |> fileTaskParam |> IO.writeValueToFile newParam
    member this.LoadTaskParam id : Param =
        id |> fileTaskParam |> IO.readValueFromFile

    member this.SaveWordPool id (pool : Word Pool) =
        id |> fileTaskWordPool |> IO.writeValueToFile pool
    member this.LoadWordPool id : Word Pool =
        id |> fileTaskWordPool |> IO.readValueFromFile
    
    member this.SavePatternPool id (pool : Pattern Pool) =
        id |> fileTaskPatternPool |> IO.writeValueToFile pool
    member this.LoadPatternPool id : Pattern Pool =
        id |> fileTaskPatternPool |> IO.readValueFromFile

    member this.SaveWordCache id (cache: Cache<Word, Pattern>) =
        id |> fileTaskWordCache |> IO.writeValueToFile cache

    member this.SavePatternCache id (cache: Cache<Pattern, Word>) =
        id |> fileTaskPatternCache |> IO.writeValueToFile cache

    member this.LoadRepoIdOfTask taskId : int =
        taskId |> fileTaskRepoId |> IO.readValueFromFile
