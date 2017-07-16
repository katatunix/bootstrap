namespace Bootstrap.Core

open NghiaBui.Common

type FinishReason = EnoughIterations | NoMoreBestPatterns

type Progress =
    abstract BeginLearning              : int -> int -> int -> Param -> unit
    abstract BeginIter                  : int -> unit

    abstract BeginGenNewPatterns        : int -> unit
    abstract EndGenNewPatterns          : IHS<Pattern> -> int -> unit

    abstract BeginSelectBestPatterns    : int -> unit
    abstract EndSelectBestPatterns      : (Pattern * float * bool * bool) [] -> float
                                            -> (Pattern * float * bool * bool) [] -> unit

    abstract BeginExtractNewWords       : unit -> unit
    abstract EndExtractNewWords         : IHS<Word> -> unit

    abstract BeginSelectBestWords       : unit -> unit
    abstract EndSelectBestWords         : (Word * float) [] -> unit

    abstract EndBootstrap               : FinishReason -> IHS<Word> -> IHS<Pattern> -> unit

    abstract BeginPruning               : int -> int -> unit
    abstract EndPruning                 : (Pattern * float) [] -> float
                                            -> (Pattern * float) [] -> (Pattern * float) [] -> unit

    abstract BeginFinalExtract          : unit -> unit
    abstract EndLearning                : IHS<Word> -> IHS<Word> -> IHS<Word> -> unit
