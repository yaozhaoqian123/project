package commander;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import core.*;
import gitobject.Branch;
import gitobject.Commit;

public class CLI {	
	
	/**
	 * Command 'jit init'
	 * @param args
	 * @throws IOException
	 */
	public static void jitInit(String[] args) throws IOException {
		String path = "";
		if(args.length <= 2) { //get default working path
			path = new File(".").getCanonicalPath();
			JitInit.init(path);
		}else if(args[2].equals("-help")){ //see help
			System.out.println("usage: jit init [<path>] [-help]\r\n" +
					"\r\n" + "jit init [<path>]:	Create an empty jit repository or reinitialize an existing one in the path or your default working directory.");
		}else {
			path = args[2];
			if(!new File(path).isDirectory()) { //if the working path input is illegal
				System.out.println(path + "is not a legal directory. Please init your reposiroty again. See 'jit init -help'.");
			}else {
				JitInit.init(path);
			}
		}
	}

	/**
	 * Command 'jit add'. Add a list of files or directories to index tree.
	 * @param args
	 */
	public static void jitAdd(String[] args) {

		if(args.length <= 2 || (args.length > 2 && args[2].equals("-help"))) {
			System.out.println("usage: jit add <file1> [<file2>...] [-help]\r\n" +
					"\r\n" + "jit add <file1> [<file2>...]: Add file(s) to stage.");
		}
		// jit add <file1> [<file2>...]
		else {
			for(int i = 2; i < args.length; i++) {
				String fileName = args[i];
				try {
					JitAdd.add(fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Command 'jit remove'. Remove file(blob) or directory(Tree) from stage area (index tree).
	 * @param args
	 */
	public static void jitRm(String[] args) {

		if(args.length <= 2 || (args.length > 2 && args[2].equals("-help"))) {
			System.out.println("usage: jit rm <file1> [<file2>...] [-help]\r\n" +
					"\r\n" + "jit rm <file1> [<file2>...]: Remove files or directories from stage area.");
		}
		// jit rm <file1> [<file2>...]
		else {
			for(int i = 2; i < args.length; i++) {
				String fileName = args[i];
				try {
					JitRm.rm(fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Command 'jit commit'. Commit files from stage to repository(objects/).
	 * @param args
	 * @throws Exception
	 */
	public static void jitCommit(String[] args) throws Exception {
		if(args.length <= 2 || (args.length > 2 && args[2].equals("-help"))) {
			System.out.println("usage: jit commit [-help]\r\n" +
					"\r\n" + "jit commit: Commit file(s) from stage to repository.");

		}
		//jit commit -m message
		else if (args.length == 4 && args[2].equals("-m")){
			String message = args[3];
			JitCommit.commit("Yue","Yue",message);
		}
	}

	/**
	 * Command 'jit branch'
	 * @param args
	 * @throws IOException
	 */
	public static void jitBranch(String[] args) throws IOException {
		if(args.length < 2 || (args.length > 2 && args[2].equals("-help"))) { //'jit branch -help'
			System.out.println("usage: jit branch [branch-name] [-d branch-name] [-help]\r\n" +
					"\r\n" +
					"jit branch: List all branches.\r\n" +
					"\r\n" +
					"jit branch [branch-name]: Create new branch.\r\n" +
					"\r\n" +
					"jit branch -d [branch-name]: Delete the branch.");
		}else if(args.length == 2) {
			//'jit branch'
			JitBranch.listbranch();
		}else if(args.length == 3) {
			//'jit branch [branch-name]'
			String branchName = args[2];
			JitBranch.createbranch(branchName);
		}else if(args.length == 4 && args[2].equals("-d")) {
			//'jit branch -d [branch-name]'
			String branchName = args[3];
			JitBranch.deleteBranch(branchName);
		}
	}

	/**
	 * Command 'jit checkout [branch]
	 * 'jit checkout -b [branch]'
	 * @param args
	 * @throws IOException
	 */
	public static void jitCheckout(String[] args) throws IOException {
		if(args.length <= 2 || (args.length > 2 && args[2].equals("-help"))) { //'jit checkout -help'
			System.out.println("usage: jit checkout [branch-name] [-b branch-name] [-help]\r\n" +
					"\r\n" +
					"jit checkout [branch]: Switch to the branch.\r\n" +
					"\r\n" +
					"jit checkout -b [branch]: Delete the branch.");
		}
		//'jit checkout [branch]'
		else if(args.length == 3) {
			String branchName = args[2];
			JitCheckout.checkout(branchName);
			System.out.println("Switched to branch " + branchName);
		}
		//'jit checkout -b [branch]'
		else if(args.length == 4 && args[2].equals("-b")) {
			String branchName = args[3];
			JitCheckout.checkout_b(branchName);
			System.out.println("Built and switched to branch " + branchName);
		}
	}

	/**
	 * Command 'jit log': list the commit history along the commit tree starting from head.
	 * @param args
	 * @throws Exception
	 */
	public static void jitLog(String[] args) throws Exception {
		//'jit log'
		if(args.length == 2) {
			JitLog.log();
		}else if (args.length == 3 && args[2].equals("-help")) {
			System.out.println("usage: jit log [HEAD commit] [number] [-help]\r\n" +
					"\r\n" +
					"jit log: List the commit history along the commit tree starting from head.");
		}
	}

	/**
	 * Command 'jit diff --cached': Show the difference between stage and the last commit.
	 * 'jit diff [first-branch] [second-branch]': Show the difference between two branches.
	 * @param args
	 * @throws IOException
	 */
	public static void jitDiff(String[] args) throws IOException {
		if(args.length < 3 || (args.length >= 3 && args[2].equals("-help"))) {
			System.out.println("usage: jit diff [--cached] [branch-name1] [branch-name2] [-help]\r\n" +
					"\r\n" +
					"jit diff --cached: Show the difference between stage and the last commit.\r\n" +
					"\r\n" +
					"jit diff [first-branch] [second-branch]: Show the difference between two branches.");
		}
		//'jit diff --cached'
		else if(args[2].equals("--cached")) {
			JitDiff.diffCached();
		}
		//'jit diff [first-branch] [second-branch]'
		else if(args.length == 4) {
			JitDiff.diffBranch(args[2], args[3]);
		}
	}

	/**
	 * Command 'jit reset --soft [commit-Id]'
	 * Command 'jit reset --mixed [commit-Id]'
	 * Command 'jit reset --hard [commit-Id]'
	 * Command 'jit reset [mode] HEAD^'
	 * @param args
	 * @throws Exception
	 */
	public static void jitReset(String[] args) throws Exception {
		if(args.length <= 2 || args[2].equals("-help")) {
			System.out.println("usage: jit reset [--soft]/[--mixed]/[--hard] [commit-id] [-help]\r\n" +
					"\r\n" +
					"jit reset --soft [commit-Id]: Reset the head file to a certain commit.\r\n" +
					"\r\n" +
					"jit reset --mixed [commit-Id]: Reset the index and head file to a certain commit.\r\n" +
					"\r\n" +
					"jit reset --hard [commit-Id]: Reset the head file , worktree and index to a certain commit." + "\r\n" +
					"\r\n" +
					"jit reset [mode] HEAD^ : Reset to the previous commit according to the mode.");
		}

		else if (args.length == 4 && args[3].equals("HEAD<")) {
			switch (args[2]) {
				case "--soft":
					JitReset.resetOneStep("soft");
					break;
				case "--mixed":
					JitReset.resetOneStep("mixed");
					break;
				case "--hard":
					JitReset.resetOneStep("hard");
					break;
			}

		}

		else if(args.length == 4 && args[2].equals("--soft")) {
			JitReset.reset(args[3],"soft");
		}

		else if(args.length == 4 && args[2].equals("--mixed")) {
			JitReset.reset(args[3],"mixed");
		}

		else if(args.length == 4 && args[2].equals("--hard")) {
			JitReset.reset(args[3],"hard");
		}
	}

	/**
	 * Command 'jit ls-files': Show the files in stage area.
	 * @param args
	 * @throws IOException
	 */

	public static void jitLs(String[] args) {
		if(args.length == 3 && args[2].equals("-help")) {
			System.out.println("usage: jit ls-files [-help]\r\n" +
					"\r\n" +
					"jit ls-files : show the files in stage area.");
		} else if (args[1].equals("ls-files")) {
			JitLs.lsfiles();
		}
	}



	/**
	 * Command 'jit help'.
	 */
	public static void jitHelp() {
		System.out.println("usage: jit [--version] [--help] [-C <path>] [-c name=value]\r\n" +
				"           [--exec-path[=<path>]] [--html-path] [--man-path] [--info-path]\r\n" +
				"           [-p | --paginate | --no-pager] [--no-replace-objects] [--bare]\r\n" +
				"           [--git-dir=<path>] [--work-tree=<path>] [--namespace=<name>]\r\n" +
				"           <command> [<args>]\r\n" +
				"\r\n" +
				"These are common Jit commands used in various situations:\r\n" +
				"\r\n" +
				"start a working area\r\n" +
				"   init       Create an empty Jit repository or reinitialize an existing one\r\n" +
				"\r\n" +
				"work on the current change\r\n" +
				"   add        Add file contents to the index\r\n" +
				"   reset      Reset current HEAD to the specified state\r\n" +
				"   rm         Remove files from the working tree and from the index\r\n" +
				"\r\n" +
				"examine the history and state\r\n" +
				"   log        Show commit logs\r\n" +
				"   status     Show the working tree status\r\n" +
				"\r\n" +
				"grow, mark and tweak your common history\r\n" +
				"   branch     List, create, or delete branches\r\n" +
				"   checkout   Switch branches or restore working tree files\r\n" +
				"   commit     Record changes to the repository\r\n" +
				"   diff       Show changes between commits, commit and working tree, etc\r\n" +
				"   merge      Join two or more development histories together\r\n" +
				"\r\n" +
				"'jit help -a' and 'jit help -g' list available subcommands and some\r\n" +
				"concept guides. See 'jit help <command>' or 'jit help <concept>'\r\n" +
				"to read about a specific subcommand or concept.");
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length <= 1 || args[1].equals("help")) {
			jitHelp();
		}else {
			if(args[1].equals("init")) {
				jitInit(args);
			}else if(args[1].equals("add")){
				jitAdd(args);
			}else if(args[1].equals("remove")) {
				jitRm(args);
			}else if(args[1].equals("commit")) {
				jitCommit(args);
			}else if(args[1].equals("branch")) {
				jitBranch(args);
			}else if(args[1].equals("checkout")) {
				jitCheckout(args);
			}else if(args[1].equals("log")) {
				jitLog(args);
			}else if(args[1].equals("diff")) {
				jitDiff(args);
			}else if(args[1].equals("reset")) {
				jitReset(args);
			}else if (args[1].equals("ls-files")) {
				jitLs(args);
			}
			else {
				System.out.println("jit: " + args[1] + "is not a git command. See 'git help'.");
			}
		}
	}
}
