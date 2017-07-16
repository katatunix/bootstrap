namespace Bootstrap.Core.Test

open NUnit.Framework

open NghiaBui.Common
open Bootstrap.Core

module Learning =

    [<Test>]
    let ``example 1`` () =
        let logs = [
            "Mar 10 16:49:29 mcdavid su(pam_unix)[9596]: session opened for user root by (uid=500)"
            "Mar 10 16:50:01 mcdavid crond(pam_unix)[9638]: session opened for user root by (uid=0)"
            "Mar 10 16:50:01 mcdavid hello(pam_unix)[9638]: session opened for user root by (uid=0)"
            "Mar 10 16:56:32 mcdavid ntpd[2544]: synchronized to 138.23.180.126, stratum 2" ] |> IHS
        let wordPool = ["crond"; "hello"] |> (Pool<Word> ()).AddSeeds
        let patternPool = Pool<Pattern> ()
        let param = {
            IterNum         = 10
            Threshold       = 0.0
            BestWordNum     = 5
            TimeoutSec      = None }
        let prog = ConsoleProgress ()

        learn logs wordPool patternPool param prog (Cache ()) (Cache ())

        Assert.That (patternPool.Seeds |> IHS.map string,
                        Is.EquivalentTo [ @"^(?:[^:]*:){2}\d+ \w+ (\w+)\(" ])
        Assert.That (wordPool.Seeds,
                        Is.EquivalentTo [ "crond"; "hello"; "su" ])

    [<Test>]
    let ``example 2`` () =
        let logs = [
            "Mar 10 16,49,29 mcdavid su(pam_unix)[9596]: session opened for user root by (uid=500)"
            "Mar 10 16,50,01 mcdavid crond(pam_unix)[9638]: session opened for user root by (uid=0)"
            "Mar 10 16,50,01 mcdavid hello(pam_unix)[9638]: session opened for user root by (uid=0)"
            "Mar 10 16,56,32 mcdavid ntpd(2544): synchronized to 138.23.180.126, stratum 2"
            "Mar 10 16,56,32 mcdavid su[2544]: synchronized to 138.23.180.126, stratum 2"
            "Mar 10 16,56,32 mcdavid crond[2544]: synchronized to 138.23.180.126, stratum 2" ] |> IHS
        let seedWords = ["crond"; "hello"; "su"]
        let wordPool = seedWords |> (Pool<Word> ()).AddSeeds
        let patternPool = Pool<Pattern> ()
        let param = {
            IterNum         = 10
            Threshold       = 0.7
            BestWordNum     = 5
            TimeoutSec      = None }
        let prog = ConsoleProgress ()

        learn logs wordPool patternPool param prog (Cache ()) (Cache ())

        Assert.That (patternPool.Seeds |> IHS.map string,
                        Is.EquivalentTo [ @"^(?:[^,]*,){2}\d+ \w+ (\w+)\(" ])
        Assert.That (wordPool.Seeds,
                        Is.EquivalentTo [ "crond"; "hello"; "su"; "ntpd" ])
