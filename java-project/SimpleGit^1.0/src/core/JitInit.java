package core;

import java.io.IOException;

import fileoperation.FileDeletion;
import repository.Repository;

public class JitInit {
	/**
	 * Init repository in your working area. The workTree should never be null.
	 * @param workTree
	 * @throws IOException
	 */
    public static void init(String workTree) throws IOException {
        Repository repo = new Repository(workTree);
        if(repo.exist()){
            if(repo.isDirectory()){
                FileDeletion.deleteFile(Repository.getGitDir());
            }
            else if(repo.isFile()){
                throw new IOException(".jit is a file, please check");
            }
        }
        repo.createRepo();
        System.out.println("Jit repository has been initiated successfully.");
    }
}
