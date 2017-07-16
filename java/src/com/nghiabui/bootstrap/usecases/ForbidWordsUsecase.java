package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Pool;
import com.nghiabui.bootstrap.core.Word;
import com.nghiabui.common.AppException;

import java.util.Collection;

public class ForbidWordsUsecase implements Id_Strings_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public ForbidWordsUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int taskId, Collection<String> words) {
		try {
			try {
				final Pool<Word> pool = gateway.loadWordPool(taskId);
				for (String w : words) {
					pool.addForbidden(new Word(w));
				}
				gateway.saveWordPool(taskId, pool);
				out.success();
			} catch (AppException e) {
				out.error(e.getMessage());
			}
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
