namespace Bootstrap.Core

type Param =
    {   IterNum         : int
        Threshold       : float
        BestWordNum     : int
        TimeoutSec      : int option } with

    member this.Text =
        "Param:\n" +
        (sprintf "    IterNum        = %d\n"     this.IterNum) +
        (sprintf "    Threshold      = %.2f\n"   this.Threshold) +
        (sprintf "    BestWordNum    = %d\n"     this.BestWordNum) +
        (sprintf "    TimeoutSec     = %s"       (match this.TimeoutSec with    | None -> "None"
                                                                                | Some x -> string x))
