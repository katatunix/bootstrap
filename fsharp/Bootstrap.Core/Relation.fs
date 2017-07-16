namespace Bootstrap.Core

open System.Collections.Generic
open NghiaBui.Common

[<AutoOpen>]
module Relation =

    type FriendMap<'Fri> when 'Fri : equality () =
        let map = Dictionary<'Fri, HashSet<Log>> ()
        let keys = HashSet ()

        member this.Add friend log =
            match map.TryGetValue friend with
            | true, logs ->
                logs.Add log |> ignore
            | _ -> 
                let logs = HashSet ()
                logs.Add log |> ignore
                map.Add (friend, logs)
                keys.Add friend |> ignore

        member this.Merge (other : FriendMap<'Fri>) =
            other.Iter (fun friend hisLogs ->
                match map.TryGetValue friend with
                | true, logs ->
                    logs.UnionWith hisLogs
                | _ ->
                    let logs = HashSet ()
                    logs.UnionWith hisLogs
                    map.Add (friend, hisLogs)
                    keys.Add friend |> ignore)

        member this.RemoveLogs logs =
            let bads = List ()
            this.Iter (fun friend curLogs ->
                for log in logs do curLogs.Remove log |> ignore
                if curLogs.Count = 0 then bads.Add friend |> ignore)
            for e in bads do
                map.Remove e |> ignore
                keys.Remove e |> ignore

        member this.Keys = keys |> IHS
        member this.Count = keys.Count

        member this.Get friend = map.[friend] |> IHS
        member this.TryGet friend =
            match map.TryGetValue friend with
            | true, x -> IHS x |> Some
            | _ -> None

        member private this.Iter f = for KeyValue (k, v) in map do f k v

        member this.Clear () =
            map.Clear ()
            keys.Clear ()

    type Cache<'Ele, 'Fri> when 'Ele : equality and 'Fri : equality () =
        let data = Dictionary<'Ele, FriendMap<'Fri>> ()
        let mutable isDirty = false

        member this.IsDirty = isDirty
        member this.ResetDirty () = isDirty <- false

        member this.Add element (friendMap : FriendMap<'Fri>) =
            match data.TryGetValue element with
            | true, curFriendMap ->
                if friendMap.Count > 0 then
                    curFriendMap.Merge friendMap
                    isDirty <- true
            | _ ->
                data.Add (element, friendMap)
                isDirty <- true

        member this.TryGet element =
            match data.TryGetValue element with
            | true, x -> Some x
            | _ -> None

        member this.Get element = data.[element]
        member this.Keys = data.Keys :> seq<'Ele>
        member this.Iter f = for KeyValue (k, v) in data do f k v

        member this.Retain (elements : IHS<'Ele>) =
            let bads = data.Keys
                        |> Seq.filter (fun e -> not (elements.Contains e))
                        |> Array.ofSeq
            for e in bads do
                data.Remove e |> ignore
                isDirty <- true

        member this.RemoveLogs logs =
            for friendMap in data.Values do
                friendMap.RemoveLogs logs
                isDirty <- true

        member this.ClearLogs () =
            for fm in data.Values do
                fm.Clear ()
                isDirty <- true
