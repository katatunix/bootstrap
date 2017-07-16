namespace Bootstrap.Core.Test

open NUnit.Framework

open NghiaBui.Common
open Bootstrap.Core

module GenPatterns =

    //=============================================================================================
    // Prefix

    [<Test>]
    let ``when prefix is empty`` () =
        let patterns = genPatterns "abc123 su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.None.StartsWith "^(?:")

    [<Test>]
    let ``when prefix is there`` () =
        let patterns = genPatterns "12;3,4;56,789,su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.StartsWith "^(?:[^,]*,){3}")

        let patterns =  genPatterns "12,3;4,56;789;su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.StartsWith "^(?:[^;]*;){3}")

    //=============================================================================================
    // Post-prefix

    [<Test>]
    let ``when post-prefix is empty`` () =
        let patterns = genPatterns "12;3,4;56,789,su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.Contains "{3}(")

    [<Test>]
    let ``when post-prefix is there`` () =
        let patterns = genPatterns "123, 456 su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.Contain @"{1} \d+ (")

    //=============================================================================================
    // Tag

    [<Test>]
    let ``when tag is empty`` () =
        let patterns = genPatterns "123, 456 !=su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.Contain "{1}(")

    [<Test>]
    let ``when tag starts with digit`` () =
        let patterns = genPatterns "123, 4ab=su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.Contain "{1}(")

    [<Test>]
    let ``when tag does not start with digit`` () =
        let patterns = genPatterns "123, key=su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.Contain "{1} key=(")

    //=============================================================================================
    // Word

    [<Test>]
    let ``when word is not found`` () =
        let patterns = genPatterns "session opened" "hello"
        Assert.AreEqual (0, patterns.Count)

    [<Test>]
    let ``when boundaries of word are invalid`` () =
        let patterns = genPatterns "Mar:,/! 1-0 16:49:29 su7[9596]: session opened" "su"
        Assert.AreEqual (0, patterns.Count)

    [<Test>]
    let ``when word is there and boundaries are valid`` () =
        let patterns = genPatterns "123,xyz abc:su456," "su456" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.EndsWith @"(\w+\d+),")

    //=============================================================================================
    // Suffix

    [<Test>]
    let ``when suffix is empty`` () =
        let patterns = genPatterns "su" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.EndsWith ")$")

    [<Test>]
    let ``with suffix, only the first char is genPatternseralized, literally`` () =
        let patterns = genPatterns "su-" "su" |> IHS.map string
        Assert.AreEqual (1, patterns.Count)
        Assert.That (patterns, Has.All.EndsWith @"\-")

    //=============================================================================================
    // General

    [<Test>]
    let ``when word appears many times, many patterns are genPatternserated`` () =
        let patterns =  genPatterns "Mar, 1-0 16:49,:su,(pam_unix)[9596], session su opened" "su"
                            |> IHS.map string
        Assert.AreEqual (2, patterns.Count)
        Assert.That (patterns, Has.Exactly(1).Contain "{2}")
        Assert.That (patterns, Has.Exactly(1).Contain "{4}")

    [<Test>]
    let ``genPatternserated pattern matches original word`` () =
        let log = "Si,pv,icio,us.G,Use"
        let word = "Use"
        let patterns = genPatterns log word
        Assert.AreEqual (1, patterns.Count)

        let pattern = Seq.head patterns
        let m = pattern.Match log
        Assert.IsTrue m.Success
        Assert.AreEqual (word, m.Groups.[1].Value)
