namespace Bootstrap.Prototype

open System.Collections.Generic
open NghiaBui.Common
open Bootstrap.Core

type Repo = { Name : string; Logs : HashSet<Log> }

type Task = {
    Name            : string
    WordPool        : Word Pool
    PatternPool     : Pattern Pool
    WordCache       : Cache<Word, Pattern>
    PatternCache    : Cache<Pattern, Word>
    Param           : Param }
