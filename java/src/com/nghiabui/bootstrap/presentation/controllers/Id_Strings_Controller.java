package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.usecases.Id_Strings_Usecase;

import java.util.Arrays;

public class Id_Strings_Controller {
	
	private final Id_Strings_Usecase uc;
	private final Utils util;
	
	public Id_Strings_Controller(Id_Strings_Usecase uc, Utils util) {
		this.uc = uc;
		this.util = util;
	}
	
	public void execute(String[] args) {
		if (!util.checkArgLen(args.length >= 3)) return;
		
		util.convertId(args[1]).ifPresent(
			taskId -> uc.execute(taskId, Arrays.asList(args).subList(2, args.length))
		);
	}
	
}
