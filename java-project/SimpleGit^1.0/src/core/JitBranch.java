package core;
import gitobject.*;
import repository.Repository;
import fileoperation.*;

import java.io.File;
import java.io.IOException;

public class JitBranch{
    //打印所有分支
    public static void listbranch() throws IOException {
        Branch curBranch = Branch.getCurBranch();
        File[] branchList = new File(Repository.getGitDir() + File.separator + "refs"+ File.separator + "heads").listFiles();
        for(File f : branchList) {
            if(curBranch.getBranchName().equals(f.getName())){
                System.out.println("*" + f.getName());
            }
            else{
                System.out.println(" " + f.getName());
            }
        }

    }

    //创建一个新分支
    public static void createbranch(String branchname) throws IOException{
        String curId = Head.getCurrentCommit();
        Branch newbranch = new Branch(branchname, curId);
        newbranch.writeBranch();
    }


    //删除一个分支
    public static void deleteBranch(String branchname) throws IOException{
        if(FileStatus.branchExist(branchname)){
            File branch = new File(Repository.getGitDir() + File.separator + "refs" + File.separator +"heads" + File.separator + branchname);
            branch.delete();
        }else{
            System.out.println(branchname+"does not exist.");
        }

    }
  

}