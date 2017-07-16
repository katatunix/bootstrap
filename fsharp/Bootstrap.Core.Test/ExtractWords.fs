namespace Bootstrap.Core.Test

open NUnit.Framework
open NghiaBui.Common
open Bootstrap.Core

module ExtractWords =

    [<Test>]
    let ``extract okay`` () =
        let words = extractWords "port = 1234 and 5678" (Pattern @"\w+ = (\d+) and (\d+)")
        Assert.That(words, Is.EquivalentTo ["1234"; "5678"])

    [<Test>]
    let ``extract nothing`` () =
        let words = extractWords "something" (Pattern @"\w+ = (\d+)")
        Assert.IsTrue (words.IsEmpty)

    [<Test>]
    let ``empty group would be ignored`` () =
        let words = extractWords "port = " (Pattern @"\w+ = (.*)")
        Assert.IsTrue (words.IsEmpty)
