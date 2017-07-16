package com.nghiabui.bootstrap.usecases;

public class PrintUsageUsecase {
	
	private final PrintUsageOut out;
	
	public PrintUsageUsecase(PrintUsageOut out) {
		this.out = out;
	}
	
	public void execute() {
		out .println("Usage: java -jar bootstrap.jar <action> [parameters]")
			.println();
			
		out .println("list: list all repos & tasks")
			.println();
			
		out .println("crepo <repoName>: create a new repo")
			.println("delrepo <repoId>: delete a repo and all of its tasks")
			.println("renrepo <repoId> <newRepoName>: rename a repo")
			.println("viewrepo <repoId>: view content of a repo")
			.println("al <repoId> <file>: add logs")
			.println("rl <repoId> <file>: remove logs")
			.println("cl <repoId>: clear logs")
			.println();
			
		out .println("ctask <repoId> <taskName>: create a new task for a repo")
			.println("deltask <taskId>: delete a task")
			.println("rentask <taskId> <newTaskName>: rename a task")
			.println("viewtask <taskId> [full | full logs]: view content of a task")
			.println(1, "full      : show relation between seed patterns and seed words")
			.println(1, "full logs : related logs will be shown")
			.println();
			
		out .println("aw <taskId> <word1> [word2] ...: add words")
			.println("fw <taskId> <word1> [word2] ...: forbid words")
			.println("rw <taskId> <word1> [word2] ...: remove words")
			.println("cw <taskId>: clear all words")
			.println();
			
		out .println("ap <taskId> <pattern1> [pattern2] ...: add patterns")
			.println("fp <taskId> <pattern1> [pattern2] ...: forbid patterns")
			.println("rp <taskId> <pattern1> [pattern2] ...: remove patterns")
			.println("cp <taskId>: clear all patterns")
			.println();
			
		out .println("up <taskId> <iter> <threshold>")
				.println(2, "<bestWordNumber> <timeoutSec>: update the param of a task")
			.println(1, "iter   : max number of iterations")
			.println(1, "threshold : patterns with score lower than this are ignored")
			.println(1, "bestWordNumber    : max number of best words selected for each iteration")
			.println(1, "timeoutSec : timeout of generating patterns each iteration, zero means no timeout")
			.println();
			
		out .println("run <taskId>: run the task");
	}
	
}
