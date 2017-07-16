package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.core.Param;
import com.nghiabui.bootstrap.presentation.presenters.Console;
import com.nghiabui.bootstrap.usecases.UpdateParamUsecase;

public class UpdateParamController {

	private final UpdateParamUsecase uc;
	private final Console console;
	private final Utils util;

	public UpdateParamController(UpdateParamUsecase uc, Console console, Utils util) {
		this.uc = uc;
		this.console = console;
		this.util = util;
	}

	public void execute(String[] args) {
		if (!util.checkArgLen(args.length == 6)) return;
		
		util.convertId(args[1]).ifPresent(
			taskId -> executeWithTaskId(taskId, args)
		);
	}
	
	private void executeWithTaskId(int taskId, String[] args) {
		final int iterationNumber;
		final double badScoreThreshold;
		final int bestWordNumber, genPatternTimeout;

		try {
			iterationNumber     = Integer.parseInt      (args[2]);
			badScoreThreshold   = Double.parseDouble    (args[3]);
			bestWordNumber      = Integer.parseInt      (args[4]);
			genPatternTimeout   = Integer.parseInt      (args[5]);
		} catch (NumberFormatException e) {
			console.errorln("Invalid format of arguments: " + e.getMessage());
			return;
		}
		
		uc.execute(
			taskId,
			new Param(
				iterationNumber,
				badScoreThreshold,
				bestWordNumber,
				genPatternTimeout
			)
		);
	}

}
