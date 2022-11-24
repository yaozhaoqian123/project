package gitobject;
import repository.Repository;
import gitobject.*;
import fileoperation.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Branch implements Serializable {
    //默认master
    protected String branchName = "master"; 	
    protected String commitId;
    static String path = Repository.getGitDir() + File.separator + "refs" + File.separator + "heads";

    public String getBranchName(){
        return branchName;
    }
    public String getCommitId(){
        return commitId;
    }
    public String getPath() {
        return path;
    }

    public Branch() {}

    public Branch(String branchName, String commitId){
        this.branchName = branchName;
        this.commitId = commitId;
        FileWriter.writeCompressedObj(path + branchName, this);
    }

    public static Branch deserialize(String branchName) throws IOException {
        if (!FileStatus.branchExist(branchName)) {
            System.out.println("Branch " + branchName + " not found.");
        }
        String filepath = path + File.separator + branchName;
        return FileReader.readCompressedObj(filepath, Branch.class);
    }

    public void updateBranch(String commitId){
        this.commitId = commitId;
    }

    public void writeBranch()  {
        FileWriter.writeCompressedObj(path + File.separator + branchName, this);
    }

    public static Branch getCurBranch() {
        Head head = FileReader.readCompressedObj(Head.getPath() ,Head.class);
        //获取当前head所指的branchName
        String branchName = head.getTargetName();
        Branch branch = null;
        try {
            branch = Branch.deserialize(branchName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return branch;
    }
    
}