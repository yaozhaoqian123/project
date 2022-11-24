package core;

import fileoperation.FileReader;
import gitobject.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JitDiff {
    public static void diffBranch(String brname1, String brname2) {
        try {
            Branch br1 = Branch.deserialize(brname1);
            Branch br2 = Branch.deserialize(brname2);
            Commit com1 = Commit.deserialize(br1.getCommitId());
            Commit com2 = Commit.deserialize(br2.getCommitId());
            Index ind1 = com1.getIndexTree();
            Index ind2 = com2.getIndexTree();
            compareIndex(ind1, ind2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void diffCached() throws IOException {
        Head head = Head.deserialize();
        String commitid = head.getCurrentCommit();
        Commit lastcommit = Commit.deserialize(commitid);
        Index lastindex = lastcommit.getIndexTree();
        Index cached = FileReader.readCompressedObj(Index.getPath(),Index.class);
        compareIndex(lastindex, cached);
    }


    // 比较不同的index树
    private static void compareIndex(Index ind1, Index ind2) throws IOException {
        HashMap<String, String> nkblobmap1 = ind1.getNamekey_blobmap();
        HashMap<String, String> nkblobmap2 = ind2.getNamekey_blobmap();

        for (String name : nkblobmap2.keySet()) {
            // 文件名相同的文件
            if (nkblobmap1.containsKey(name)) {
                String key1 = nkblobmap1.get(name);
                String key2 = nkblobmap2.get(name);
                // blob不等, 说明文件发生了改变.
                if (!key1.equals(key2)) {
                    Blob b1 = Blob.deserialize(key1);
                    Blob b2 = Blob.deserialize(key2);
                    // 比较之
                    compareBlob(b1, b2);
                    System.out.println();
                }
                // 比较过了，除去前一个.
                nkblobmap1.remove(name);
            }

            // index2 新增文件.
            else {
                System.out.println("diff --jit a\\null b\\" + name);
                System.out.println("new file mode 100644");
                System.out.println("--- \\dev\\null");
                System.out.println("+++ b\\" + name);
                System.out.println();
            }
        }

        // index2所缺少的
        for (String name : nkblobmap1.keySet()) {
            System.out.println("diff --jit a\\" + name + " b\\null");
            System.out.println("deleted file mode 100644");
            System.out.println("--- a\\" + name);
            System.out.println("+++ \\dev\\null");
            System.out.println();
        }
    }

    //Compare a before-and-after modification in two blobs, output its difference.

    private static void compareBlob(Blob before, Blob after) throws IOException {
        String beforeValue = before.getValue();
        String afterValue = after.getValue();
        System.out.println("diff --jit a\\"+before.getName()+"  b\\"+ after.getName());
        System.out.println("--- " + before.getName());
        System.out.println("+++ " + after.getName());
        compareValue(beforeValue, afterValue);
    }

    private static void compareValue(String beforeValue, String afterValue) throws IOException {
        ArrayList<String> beforels = FileReader.readByBufferReader(beforeValue);
        ArrayList<String> afterls = FileReader.readByBufferReader(afterValue);
        int[][] difference = compareContentDiff(beforels, afterls);
        printDiff(beforels, afterls, difference);
    }

    //Myers diff algorithm using dp.
    private static int[][] compareContentDiff(ArrayList<String> content1, ArrayList<String> content2) {

        int m = content2.size(), n = content1.size();
        int[][][] paths = new int[n + 1][][];
        int[][] dp = new int[m + 1][n + 1];


        paths[0] = new int[1][2];
        paths[0][0][0] = 0;
        paths[0][0][1] = 0;

        for (int i = 0; i <= m; ++i) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; ++j) {
            dp[0][j] = j;
            if (j == 0) {
                continue;
            }
            paths[j] = new int[paths[j - 1].length + 1][2];
            if (j > 0) {
                for (int i = 0; i <= j - 1; ++i) {
                    paths[j][i] = paths[j - 1][i];
                }
            }
            paths[j][paths[j].length - 1][0] = j;
            paths[j][paths[j].length - 1][1] = 0;
        }
        int[][] temp;
        int[][] lastPath;
        for (int i = 1; i <= m; ++i) {
            temp = paths[0].clone();
            paths[0] = new int[temp.length + 1][2];
            for (int k = 0; k < temp.length; k++) {
                paths[0][k] = temp[k].clone();
            }
            paths[0][temp.length][0] = i;
            paths[0][temp.length][1] = 0;
            for (int j = 1; j <= n; ++j) {
                temp = paths[j - 1];
                if (content1.get(j - 1).equals(content2.get(i - 1)) ) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int minRes = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                    dp[i][j] = minRes;
                    if (minRes == dp[i - 1][j - 1] + 1) {
                        paths[j] = new int[temp.length + 1][2];
                        for (int k = 0; k < temp.length; k++) {
                            paths[j][k] = temp[k].clone();
                        }
                    } else if (minRes == dp[i - 1][j] + 1) {
                        lastPath = paths[j].clone();
                        paths[j] = new int[lastPath.length + 1][2];
                        for (int k = 0; k < lastPath.length; k++) {
                            paths[j][k] = lastPath[k].clone();
                        }
                    } else {
                        paths[j] = new int[paths[j - 1].length + 1][2];
                        for (int k = 0; k < paths[j - 1].length; k++) {
                            paths[j][k] = paths[j - 1][k].clone();
                        }
                    }
                }
                paths[j][paths[j].length - 1][0] = i;
                paths[j][paths[j].length - 1][1] = j;
            }
        }

        return paths[paths.length - 1];
    }


    private static void printDiff(ArrayList<String> beforels, ArrayList<String> afterls, int[][] difference) {
        int store = 0;

        for (int i = 1; i < difference.length; i++) {
            if (difference[i-1][0] + 1 == difference[i][0] && difference[i-1][1] + 1 == difference[i][1]){
                System.out.println("   "+afterls.get(difference[i][0] - 1));

            }else if(difference[i-1][0] + 1 == difference[i][0]){
                System.out.println("+"+afterls.get(difference[i][0] - 1));
                store++;
            }else{
                System.out.println("-"+beforels.get(difference[i][1] - 1));
                store++;
            }
        }
    }
}
