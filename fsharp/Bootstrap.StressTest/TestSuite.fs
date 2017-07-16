namespace Bootstrap.StressTest

open System.IO

open NghiaBui.Common
open Bootstrap.Core

type TestOption =
    | TO_PerformanceNothing
    | TO_CachingOnly

    | TO_QualityNothing
    | TO_DynamicOnly

    | TO_Full

type TestSuite (testOption) =

    let path file = "data/" + file

    let runCase logs seedWords seedPatterns param caseName =
        ConsoleProgress.HrLine
        printfn "CASE: %s" caseName

        let wPool = seedWords     |> (Pool<Word> ()).AddSeeds
        let pPool = seedPatterns  |> Seq.map Pattern |> (Pool<Pattern> ()).AddSeeds
        let prog  = ConsoleProgress ()
        let caches = (Cache(), Cache())

        let learn =
            match testOption with
            | TO_PerformanceNothing ->
                learnWith PerformanceNothing DynamicAndPruning
            | TO_CachingOnly ->
                learnWith (CachingOnly caches) DynamicAndPruning
            
            | TO_QualityNothing ->
                learnWith (CachingAndParallel caches) QualityNothing
            | TO_DynamicOnly ->
                learnWith (CachingAndParallel caches) DynamicOnly

            | TO_Full ->
                learnWith (CachingAndParallel caches) DynamicAndPruning

        learn logs wPool pPool param prog
        printfn ""

    //========================================
    // A

    let a1LearningIpAddresses logs =
        let seedWords = [   "10.25.10.12"; "192.96.201.142"; "10.20.10.36";
                            "10.20.24.1"; "10.20.14.36"; "10.20.18.10"      ]
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 5000
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "a1LearningIpAddresses"

    let a2LearningRepeatCounts logs =
        let seedWords = [1..10] |> List.map string
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "a2LearningRepeatCount"

    let a3LearningThreatTypes logs =
        let seedWords = ["spyware"; "vulnerability"]
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "a3LearningThreatTypes"

    let a4LearningAppNames logs =
        let seedWords = ["ms-ds-smb"; "unknown-tcp"; "mssql-db"; "dns"; "web-browsing"; "sip"]
        let param = {
            IterNum         = 10
            Threshold       = 0.39
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "a4LearningAppNames"

    let a5LearningRuleNames logs =
        let seedWords = ["outbound-default"; "WSUS"; "PACS"; "MSSQL"; "FILE-SERVER"; "FICO"]
        let param = {
            IterNum         = 10
            Threshold       = 0.5
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "a5LearningRuleNames"

    //========================================
    // B

    let b1LearningServerNames logs =
        let seedWords = ["server2"; "server1"; "server11"; "server6"; "server33"; "server32"; "server10";
                            "server5"]
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "b1LearningServerNames"

    let b2LearningUserCpuUsages logs =
        let seedWords = [82..99] |> List.map (fun w -> (string w) + "%")
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "b2LearningUserCpuUsages"

    let b3LearningIpAddressed logs =
        let seedWords = ["192.171.91.29"; "192.171.29.170"; "192.168.168.245"; "192.168.169.197";
                            "192.168.169.0"; "192.168.168.76"]
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 1000
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "b3LearningIpAddressed"

    let b4LearningFreeMem logs =
        let seedWords = ["73381 MB"; "189823 MB"; "196627 MB"; "180174 MB"; "174531 MB"]
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 10000
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "b4LearningFreeMem"

    let b5LearningPacketLossPercentages logs =
        let seedWords = ["0%"; "16%"; "100%"]
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "b5LearningPINGPacketLossPercentages"

    //========================================
    // C
    let c1LearningIpAddressed logs =
        let seedWords = ["10.23.51.95"; "10.33.50.40"; "192.171.84.241"; "192.171.178.30";
                            "192.171.22.22"; "192.171.83.16"; "192.171.24.28"; "192.171.192.253";
                            "192.168.168.151"; "192.168.168.195"]
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 5000
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "c1LearningIpAddressed"

    let c2LearningServerNames logs =
        let seedWords = ["server27"; "server23"; "server11"; "server10"; "server1"; "server24";
                            "server15"; "server18"; "server35"; "server20"]
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "c2LearningServerNames"

    let c3LearningDroppedMessages logs =
        let seedWords = [   "'pipe(/dev/xconsole)=3038'"
                            "'pipe(/dev/xconsole)=0'"
                            "'pipe(/dev/xconsole)=754'" ]
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 50000
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "c3LearningDroppedMessages"

    let c4LearningProcessNames logs =
        let seedWords = ["system"; "sshd"; "vasidmapd"; "postfix/pickup"; "postfix/qmgr";
                            "/usr/sbin/cron"; "postfix/cleanup"; "syslog-ng"]
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "c4LearningProcessNames"

    let c5LearningFromUsernames logs =
        let seedWords = ["<nagios@server21>"; "<root>"; "<root@server18>"; "<nagios>"; "<>"]
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 5
            TimeoutSec      = Some 30 }
        runCase logs seedWords [] param "c5LearningFromUsernames"
    
    //========================================
    // ALL

    member this.runAllTests () =
        let file = "threat-log.txt"
        printfn "Loading log events from file %s ...\n" file
        let logs = file |> path |> File.ReadAllLines |> IHS
        a1LearningIpAddresses           logs
        a2LearningRepeatCounts          logs
        a3LearningThreatTypes           logs
        a4LearningAppNames              logs
        a5LearningRuleNames             logs

        let file = "nagios.txt"
        printfn "Loading log events from file %s ...\n" file
        let logs = file |> path |> File.ReadAllLines |> IHS
        b1LearningServerNames           logs
        b2LearningUserCpuUsages         logs
        b3LearningIpAddressed           logs
        b4LearningFreeMem               logs
        b5LearningPacketLossPercentages logs

        let file = "syslog.txt"
        printfn "Loading log events from file %s ...\n" file
        let logs = file |> path |> File.ReadAllLines |> IHS
        c1LearningIpAddressed           logs
        c2LearningServerNames           logs
        c3LearningDroppedMessages       logs
        c4LearningProcessNames          logs
        c5LearningFromUsernames         logs
