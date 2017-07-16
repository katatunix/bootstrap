package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.usecases.Id_String_Usecase;

public class Id_String_Controller {
	
	private final Id_String_Usecase uc;
	private final Utils util;
	
	public Id_String_Controller(Id_String_Usecase uc, Utils util) {
		this.uc = uc;
		this.util = util;
	}
	
	public void execute(String[] args) {
		if (!util.checkArgLen(args.length == 3)) return;
		
		util.convertId(args[1]).ifPresent(
			repoId -> uc.execute(repoId, args[2])
		);
	}
	
}
