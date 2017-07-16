package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.usecases.Id_Usecase;

public class Id_Controller {
	
	private final Id_Usecase uc;
	private final Utils util;
	
	public Id_Controller(Id_Usecase uc, Utils util) {
		this.uc = uc;
		this.util = util;
	}
	
	public void execute(String[] args) {
		if (!util.checkArgLen(args.length == 2)) return;
		
		util.convertId(args[1]).ifPresent(uc::execute);
	}
	
}
