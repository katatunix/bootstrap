package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.usecases.CreateRepoUsecase;

public class CreateRepoController {

	private final CreateRepoUsecase uc;
	private final Utils util;

	public CreateRepoController(CreateRepoUsecase uc, Utils util) {
		this.uc = uc;
		this.util = util;
	}

	public void execute(String[] args) {
		if (!util.checkArgLen(args.length == 2)) return;
		uc.execute(args[1]);
	}

}
