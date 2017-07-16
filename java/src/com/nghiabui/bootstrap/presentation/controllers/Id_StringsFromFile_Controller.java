package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.usecases.Id_Strings_Usecase;

public class Id_StringsFromFile_Controller {
	
	private final Id_Strings_Usecase uc;
	private final Utils util;
	
	public Id_StringsFromFile_Controller(Id_Strings_Usecase uc, Utils util) {
		this.uc = uc;
		this.util = util;
	}
	
	public void execute(String[] args) {
		if (!util.checkArgLen(args.length == 3)) return;
		
		util.convertId(args[1]).ifPresent(
			taskId -> util.readInputFile(args[2]).ifPresent(
				lines -> uc.execute(taskId, lines)
			)
		);
	}
	
}
