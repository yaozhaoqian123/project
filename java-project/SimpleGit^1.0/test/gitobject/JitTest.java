package gitobject;

import core.*;
import fileoperation.FileReader;
import repository.Repository;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

class JitTest {

    public static void main(String[] args) {
        try {
            JitInit.init("C:\\软件\\testjit\\SimpleGit-main\\test");
            JitAdd.add("a.txt");
            JitCommit.commit("Yue","Yue","a.txt");
            JitAdd.add("testTree");
            JitCommit.commit("Yue","Yue","testTree");


            /*
            createRepository();
            JitAdd.add("a.txt");
            JitCommit.commit("Yue","Yue","a.txt");
            JitBranch.createbranch("test");
            JitAdd.add("testTree");
            JitCommit.commit("Yue","Yue","testTree");
            JitLs.lsfiles();

            JitCheckout.checkout("test");
            JitAdd.add("b.txt");
            JitCommit.commit("Yue","Yue","b.txt");

            JitDiff.diffBranch("master", "test");

            JitReset.resetOneStep("hard");

            writeRandomString("a.txt");
            JitAdd.add("a.txt");

            JitDiff.diffCached();
*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createRepository() throws IOException {
        JitInit.init("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository");
    }


    public static void createBlob() throws Exception {
        File f =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" + "a.txt");
        Blob b = new Blob(f);
        System.out.println("Before serialization:");
        System.out.println(b.getKey());
        System.out.println(b.getName());
        System.out.println(b.getValue());
        b.compressWrite();

        System.out.println("After deserialization:");
        Blob b1 = Blob.deserialize(b.getKey());
        System.out.println(b1.getKey());
        System.out.println(b1.getName());
        System.out.println(b1.getValue());
    }


    public static void createTree() throws Exception {
        File f =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree");
        Tree t =  new Tree(f);

        System.out.println("Before serialization:");
        System.out.println(t.getKey());
        System.out.println(t.getName());
        System.out.println(t.getValue());
        t.compressWrite();

        System.out.println("After deserialization:");
        Tree t1 = Tree.deserialize(t.getKey());
        System.out.println(t1.getKey());
        System.out.println(t1.getName());
        System.out.println(t1.getValue());
    }

    public static void createCommit() throws Exception{
        Commit com = new Commit("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree",
                "Yue", "Yue", "initial commit.");
        System.out.println("Before serialization:");
        System.out.println(com.getKey());
        System.out.println(com.getValue());
        com.compressWrite();

        System.out.println("After deserialization:");
        Commit com1 = Commit.deserialize(com.getKey());
        System.out.println(com1.getKey());
        System.out.println(com1.getValue());

        /*
        File f =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree" + File.separator + "testTree1");
        Tree t =  new Tree(f);

        Commit com2 = new Commit(t, "Yue", "Yue", "initial commit.");
        System.out.println("Before serialization:");
        System.out.println(com2.getKey());
        System.out.println(com2.getValue());
        com2.compressWrite();

        System.out.println("After deserialization:");
        Commit com3 = Commit.deserialize(com2.getKey());
        System.out.println(com3.getKey());
        System.out.println(com3.getValue());
         */
    }

    public static void testAdd(){
        try {
            //新增
            System.out.println("新增a.txt");
            JitAdd.add("a.txt");
            //读入index文件
            Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);
            showIndex(index);

            //新增testTree\\a.txt
            System.out.println("新增testTree\\a.txt: ");
            JitAdd.add("testTree\\a.txt");
            //读入index文件
            showIndex(FileReader.readCompressedObj(Index.getPath(),Index.class));

            //新增testTree\\testTree1
            System.out.println("新增testTree\\testTree1: ");
            JitAdd.add("testTree\\testTree1");
            //读入index文件
            showIndex(FileReader.readCompressedObj(Index.getPath(),Index.class));

            /*

            System.out.println("修改a.txt: ");
            //修改a.txt
            writeRandomString("a.txt");
            JitAdd.add("a.txt");
            //读入index文件
            showIndex(FileReader.readCompressedObj(Index.getPath(),Index.class));

            //修改testTree\\testTree1
            System.out.println("修改testTree\\testTree1: ");
            writeRandomString("testTree" + File.separator + "testTree1" + File.separator + "d.txt");
            JitAdd.add("testTree" + File.separator + "testTree1");
            //读入index文件
            showIndex(FileReader.readCompressedObj(Index.getPath(),Index.class));


            //修改testTree\\testTree1\\d.txt
            System.out.println("修改testTree\\testTree1\\d.txt:  ");
            writeRandomString("testTree" + File.separator + "testTree1" + File.separator + "d.txt");
            JitAdd.add("testTree" + File.separator + "testTree1" + File.separator + "d.txt");
            //读入index文件
            showIndex(FileReader.readCompressedObj(Index.getPath(),Index.class));
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCommit() {
        testIndex();

        JitCommit.commit("Yue", "Yue", "Initial Commit.");

        //检查Head指针是否指向最新Commit
        Head head1 = FileReader.readCompressedObj(Head.getPath(),Head.class);

        System.out.println("上传Commit1后HEAD指针为：");
        System.out.println(head1.getCurrentCommit());

        testIndex();

        JitCommit.commit("Tengyue", "Tengyue", "Second Commit.");

        //检查Head指针是否指向最新Commit
        Head head2 = FileReader.readCompressedObj(Head.getPath(),Head.class);

        System.out.println("上传Commit2后HEAD指针为：");
        System.out.println(head2.getCurrentCommit());

        System.out.println("Commit2为：");
        Commit second = Commit.deserialize(head2.getCurrentCommit());
        System.out.println("Second Commit:");
        System.out.println(second.getValue());
    }

    public static void testIndex() {
        try {
            JitAdd.add("a.txt");
            JitAdd.add("testTree");
            //读入index文件
            Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);

            showIndex(index);

            //删除a.txt
            System.out.println("Delete a.txt: ");
            index.deleteFile("a.txt");

            showIndex(index);


            //删除 testTree/testTree1 文件夹.
            System.out.println("Delete testTree1: ");
            index.deleteDirectory("testTree" + File.separator + "testTree1");

            showIndex(index);
            /*
            //删除 testTree
            System.out.println("testTree deleted.");
            index.deleteDirectory("testTree");
            showIndex(index);
            */


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testHash() {
        File blobfile =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" + "a.txt");
        try {
            Blob b = new Blob(blobfile);
            System.out.println("a.txt key值为：");
            System.out.println(b.getKey());
            JitHash.hash("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" + "a.txt");

            File treefile =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree");
            Tree t =  new Tree(treefile);
            System.out.println("testTree key值为：");
            System.out.println(t.getKey());
            JitHash.hash("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void testRm() {
        try {
            JitAdd.add("a.txt");
            JitAdd.add("testTree");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读入index文件
        Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);
        showIndex(index);

        //删除 testTree/testTree1 文件夹.
        System.out.println("testTree1 deleted.");
        try {
            JitRm.rm("testTree" + File.separator + "testTree1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读入删除后的index文件
        Index indexrm1 = FileReader.readCompressedObj(Index.getPath(),Index.class);
        showIndex(indexrm1);

        //删除a.txt
        System.out.println("a.txt deleted.");
        try {
            JitRm.rm("a.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读入删除后的index文件
        Index indexrm2 = FileReader.readCompressedObj(Index.getPath(),Index.class);
        showIndex(indexrm2);
    }

    public static void testLog() {
        try {
            JitAdd.add("a.txt");
            JitCommit.commit("Yue","Yue","initial commit.");
            JitAdd.add("testTree\\a.txt");
            JitCommit.commit("Yue","Yue","testTree\\a.txt commit.");
            JitAdd.add("testTree\\testTree1");
            JitCommit.commit("Yue","Yue","testTree\\testTree1 commit.");
            JitAdd.add("testTree");
            JitCommit.commit("Yue","Yue","testTree completed.");
            JitLog.log();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void testBranch() {

    }
    public static void testDiff() throws Exception {
        JitAdd.add("a.txt");
        JitCommit.commit("Yue","Yue","initial commit.");
        JitAdd.add("testTree");
        JitDiff.diffCached();
        JitCommit.commit("Yue","Yue","second commit.");
        JitBranch.createbranch("test");
        JitCheckout.checkout("test");
        writeRandomString("a.txt");
        JitAdd.add("a.txt");
        JitCommit.commit("Yue","Yue","a.txt modified.");
        JitDiff.diffBranch("master", "test");

    }


    public static void showIndex(Index in) {
        //显示暂存区文件
        System.out.println("Valuemap:");
        in.show();
        System.out.println("name_key_map:");
        in.showIndexMap();
        System.out.print("\n");
        //显示blobMap 和 TreeMap
        System.out.println("Root: ");
        in.traverse();
    }

    public static void writeRandomString(String pathname) {
        String path = Repository.getWorkTree();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String number = "0123456789";
        int cnt = (int)(40 * Math.random()) + 10;
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < cnt; i ++) {
            int alphabetid = (int) (alphabet.length() * Math.random());
            bf.append(alphabet.charAt(alphabetid));
            if (alphabetid <= 9) bf.append(number.charAt(alphabetid));
        }
        try (PrintWriter out = new PrintWriter(path + File.separator + pathname)) {
            out.println(bf.toString());
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

}
