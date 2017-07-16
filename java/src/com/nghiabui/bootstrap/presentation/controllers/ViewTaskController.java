package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.usecases.ViewTaskUsecase;

public class ViewTaskController {

	private final ViewTaskUsecase uc;
	private final Utils util;

	public ViewTaskController(ViewTaskUsecase uc, Utils util) {
		this.uc = uc;
		this.util = util;
	}

	public void execute(String[] args) {
		if (!util.checkArgLen(args.length >= 2)) return;

		util.convertId(args[1]).ifPresent(taskId -> {
			final boolean isFull = args.length >= 3;
			final boolean viewsLogs = args.length >= 4;
			uc.execute(taskId, isFull, viewsLogs);
		});
	}

}
