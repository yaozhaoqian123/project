package core;

import fileoperation.FileReader;
import gitobject.*;

public class JitCommit {
    public static void commit(String author, String committer, String message) {
        //读入index文件
        Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);

        try {
            Commit com = new Commit(index, author, committer, message);
            // 将暂存区生成的树、commit类、暂存区内包含的所有的树全部存入objects文件夹中
            index.compressWriteAsTree();
            com.compressWrite();

            writeTree(index);
            //读出HEAD当前所指向的Branch， 将branch向后一步, 最后覆写.
            Branch curbranch = Branch.getCurBranch();
            curbranch.updateBranch(com.getKey());
            curbranch.writeBranch();
            System.out.println("[" +curbranch.getBranchName() + " " + com.getKey().substring(0, 7) + "] " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // commit后将暂存区所有跟踪的Tree文件写入objects文件
    private static void writeTree(Tree root) throws Exception {
        for (Tree t : root.getTreeMap().values()) {
            t.compressWrite();
            writeTree(t);
        }
    }
}
