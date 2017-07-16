package com.nghiabui.bootstrap;

import com.nghiabui.bootstrap.presentation.controllers.*;
import com.nghiabui.bootstrap.datasource.SerializeGateway;
import com.nghiabui.bootstrap.presentation.presenters.*;
import com.nghiabui.bootstrap.usecases.*;
import com.nghiabui.bootstrap.presentation.ConsoleImpl;
import com.nghiabui.common.AppException;
import com.nghiabui.common.Ticker;
import com.nghiabui.common.io.Folder;

public class Main {

	public static void main(String[] args) {
		final Console console = new ConsoleImpl(new com.nghiabui.common.Console());
		final Ticker ticker = new Ticker();
		if (args.length == 0) {
			printUsage(console);
		} else {
			handle(args, console);
		}
		console.infoln().infoln("Total time: " + ticker.elapse() + "s");
	}
	
	private static void handle(String[] args, Console console) {
		final Folder dataFolder = new Folder("data");
		try {
			dataFolder.createIfNotExist();
		} catch (AppException e) {
			console.errorln(e.getMessage());
			return;
		}
		
		final Gateway gateway = new SerializeGateway(dataFolder);
		final Utils util = new Utils(console);
		
		switch (args[0]) {
			case "list": {
				new ListAllUsecase(gateway, new ListAllPresenter(console)).execute();
				break;
			}
			//=====================================================================================================
			case "crepo": {
				final CreateRepoUsecase uc = new CreateRepoUsecase(gateway, new CreatePresenter(console));
				new CreateRepoController(uc, util).execute(args);
				break;
			}
			case "delrepo": {
				final DeleteRepoUsecase uc = new DeleteRepoUsecase(gateway, new BasicPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			case "renrepo": {
				final RenameRepoUsecase uc = new RenameRepoUsecase(gateway, new BasicPresenter(console));
				new Id_String_Controller(uc, util).execute(args);
				break;
			}
			case "viewrepo": {
				final ViewRepoUsecase uc = new ViewRepoUsecase(gateway, new ViewRepoPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			case "al": {
				final AddLogsUsecase uc = new AddLogsUsecase(gateway, new BasicPresenter(console));
				new Id_StringsFromFile_Controller(uc, util).execute(args);
				break;
			}
			case "rl": {
				final RemoveLogsUsecase uc = new RemoveLogsUsecase(gateway, new BasicPresenter(console));
				new Id_StringsFromFile_Controller(uc, util).execute(args);
				break;
			}
			case "cl": {
				final ClearLogsUsecase uc = new ClearLogsUsecase(gateway, new BasicPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			//=====================================================================================================
			case "ctask": {
				final CreateTaskUsecase uc = new CreateTaskUsecase(gateway, new CreatePresenter(console));
				new Id_String_Controller(uc, util).execute(args);
				break;
			}
			case "deltask": {
				final DeleteTaskUsecase uc = new DeleteTaskUsecase(gateway, new BasicPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			case "rentask": {
				final RenameTaskUsecase uc = new RenameTaskUsecase(gateway, new BasicPresenter(console));
				new Id_String_Controller(uc, util).execute(args);
				break;
			}
			case "viewtask": {
				final ViewTaskUsecase uc = new ViewTaskUsecase(gateway, new ViewTaskPresenter(console));
				new ViewTaskController(uc, util).execute(args);
				break;
			}
			//=====================================================================================================
			case "aw": {
				final AddWordsUsecase uc = new AddWordsUsecase(gateway, new BasicPresenter(console));
				new Id_Strings_Controller(uc, util).execute(args);
				break;
			}
			case "fw": {
				final ForbidWordsUsecase uc = new ForbidWordsUsecase(gateway, new BasicPresenter(console));
				new Id_Strings_Controller(uc, util).execute(args);
				break;
			}
			case "rw": {
				final RemoveWordsUsecase uc = new RemoveWordsUsecase(gateway, new BasicPresenter(console));
				new Id_Strings_Controller(uc, util).execute(args);
				break;
			}
			case "cw": {
				final ClearWordsUsecase uc = new ClearWordsUsecase(gateway, new BasicPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			//=====================================================================================================
			case "ap": {
				final AddPatternsUsecase uc = new AddPatternsUsecase(gateway, new BasicPresenter(console));
				new Id_Strings_Controller(uc, util).execute(args);
				break;
			}
			case "fp": {
				final ForbidPatternsUsecase uc = new ForbidPatternsUsecase(gateway, new BasicPresenter(console));
				new Id_Strings_Controller(uc, util).execute(args);
				break;
			}
			case "rp": {
				final RemovePatternsUsecase uc = new RemovePatternsUsecase(gateway, new BasicPresenter(console));
				new Id_Strings_Controller(uc, util).execute(args);
				break;
			}
			case "cp": {
				final ClearPatternsUsecase uc = new ClearPatternsUsecase(gateway, new BasicPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			//=====================================================================================================
			case "up": {
				final UpdateParamUsecase uc = new UpdateParamUsecase(gateway, new BasicPresenter(console));
				new UpdateParamController(uc, console, util).execute(args);
				break;
			}
			case "run": {
				final RunTaskUsecase uc = new RunTaskUsecase(gateway, new RunTaskPresenter(console));
				new Id_Controller(uc, util).execute(args);
				break;
			}
			default:
				printUsage(console);
		}
	}
	
	private static void printUsage(Console console) {
		new PrintUsageUsecase(new PrintUsagePresenter(console)).execute();
	}

}
