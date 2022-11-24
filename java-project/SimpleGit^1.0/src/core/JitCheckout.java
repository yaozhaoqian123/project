package core;
import fileoperation.*;
import gitobject.*;
import repository.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class JitCheckout{
    //转向某分支
    public static void checkout(String branchname) throws IOException {
        if(FileStatus.branchExist(branchname)){
            //更新head指向的分支
            Branch branch = Branch.deserialize(branchname);
            Head head = Head.deserialize();
            head.updateTarget(branch);
            head.compressWrite();

            //得到对应commitId
            String commitkey = Head.getCurrentCommit();
            Commit com = Commit.deserialize(commitkey);

            //更新的暂存区,将暂存区回到分支指向的状态。
            Index cur_index = com.getIndexTree();
            cur_index.compressWrite();


            //清空工作区所有文件，把commitID中的内容还原出来。
            File[] worktree = new File(Repository.getWorkTree()).listFiles();
            // 已被跟踪的文件
            Status status = Status.deserialize();
            HashSet<String> trackedSet = status.getTrackedSet();

            for(File f : worktree){
                if (!trackedSet.contains(f.getName()) || f.getName().equals(".jit")) continue;

                FileDeletion.deleteFile(f);
            }

            FileCreation.recoverWorkTree(cur_index, Repository.getWorkTree());

        }else{
            System.out.println(branchname + "does not exist.");
        }

    }

    public static void checkout_b(String branchname)throws IOException {
        try{
            JitBranch.createbranch(branchname);
            checkout(branchname);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
}