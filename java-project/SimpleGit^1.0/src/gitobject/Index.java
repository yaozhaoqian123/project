package gitobject;

import fileoperation.FileReader;
import fileoperation.FileWriter;
import repository.Repository;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Index extends Tree implements Serializable {
    static String path = Repository.getGitDir() + File.separator + "index";   //absolute path of index.
    protected HashMap<String, String> namekey_blobmap = new HashMap<>();  // 所有blob节点的 <name, key>
    protected HashMap<String, String> namekey_treemap = new HashMap<>();  // 所有tree节点的 <name, key>
    //储存<文件名称,暂存区value>
    //value格式： mode hash值 文件命名 上传时间
    //使用LinkedHashMap,便于索引以及按上传时间顺序展示暂存区信息.
    LinkedHashMap<String,String> valuemap = new LinkedHashMap<>();

    public Index() {}
    public static String getPath() {
        return path;
    }

    public HashMap<String, String> getNamekey_blobmap() {
        return namekey_blobmap;
    }

    public HashMap<String, String> getNamekey_treemap() {
        return namekey_treemap;
    }

    //加入某个GitObject,新增或修改
    public void add(GitObject go) throws Exception{
        String fmt = go.getFmt();
        String name = go.getName();
        String key = go.getKey();

        Status status = Status.deserialize();
        status.add(name);
        status.writeStatus();

        // 获得命令执行时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());

        if (FileReader.objectExists(key)) return;

        if (fmt.equals("blob")) {
            //对应文件已经存在，提交修改内容，写入新的Blob， 更新对应的Tree 以及index内的map.
            if (namekey_blobmap.containsKey(name)) {
                go.compressWrite();
                String target_key = namekey_blobmap.get(name);
                addBlobWithSameName(target_key, key, name, namekey_blobmap);
                updateMap(go, date);
            }
            // 对应文件不存在,判断父文件夹的位置，以及是否已经在index树中，如果没有，则创立新树
            else {
                String [] parentList = name.replace("\\","/").split("/");
                if (parentList.length <= 1) {
                    go.compressWrite();
                    blobMap.put(key, name);
                    updateMap(go, date);
                }
                else {

                    List <String []> res = splitParentList(parentList);
                    String[] existedParent = res.get(0);
                    String[] newParent = res.get(1);

                    if(existedParent.length == 0) {
                        Tree root = createRecursiveNewTree(parentList,1, parentList[0], go);
                        treeMap.put(root.getKey(), root);
                        updateMap(root, date);
                    }
                    else if (newParent.length == 1){
                        String parent_key = getParentKey(existedParent);
                        addNewBlob(parent_key, key, name, namekey_blobmap);
                        updateMap(go, date);
                    }
                    else {
                        String parent_key = getParentKey(existedParent);
                        Tree recur_new_tree = createRecursiveNewTree(newParent, 1, newParent[0], go);
                        addNewTree(parent_key, recur_new_tree.getKey(), recur_new_tree, namekey_blobmap);
                        updateMap(go, date);
                    }

                }
                //更新index的 value 和key值
                update();
            }
        }
        else if (fmt.equals("tree")) {
            //对应文件夹已经存在，找到该tree含有的不同文件，写入objects/, 替换相应的treeMap位置,更新上层的树.
            if (namekey_treemap.containsKey(name)) {
                String target_key = namekey_treemap.get(name);
                addTreeWithSameName(target_key, key, (Tree) go, namekey_treemap);
                updateMap(go, date);
            }

            //对应文件夹不存在，判断父文件夹的位置，以及是否已经在index树中，如果没有，则创立新树
            else {
                String [] parentList = name.replace("\\","/").split("/");
                if (parentList.length <= 1) {
                    treeMap.put(key, (Tree) go);
                    updateMap(go, date);
                }
                else {

                    List <String []> res = splitParentList(parentList);
                    String[] existedParent = res.get(0);
                    String[] newParent = res.get(1);

                    if(existedParent.length == 0) {
                        Tree root = createRecursiveNewTree(parentList,1, parentList[0], go);
                        treeMap.put(root.getKey(), root);
                        updateMap(root, date);
                    }
                    else if (newParent.length == 1){
                        String parent_key = getParentKey(existedParent);
                        addNewTree(parent_key, key, (Tree) go, namekey_treemap);
                        updateMap(go, date);
                    }

                    else {
                        String parent_key = getParentKey(existedParent);
                        Tree recur_new_tree = createRecursiveNewTree(newParent, 1, newParent[0], go);
                        addNewTree(parent_key, recur_new_tree.getKey(), recur_new_tree, namekey_treemap);
                        updateMap(go, date);
                    }
                }
                //更新index的 value 和key值
                update();
            }
        }
    }

    private List<String[]> splitParentList(String[] parentList) {
        int i;
        String compared_name = "";
        for (i = 0; i < parentList.length; i ++) {
            if (i == 0) compared_name = parentList[i];
            else compared_name += File.separator + parentList[i];
            if (!namekey_treemap.containsKey(compared_name)) {
                break;
            }
        }
        String [] existedParent = new String[i];
        String [] newParent = new String[parentList.length - i];
        for (int a = 0; a < existedParent.length; a ++) {
            existedParent[a] = parentList[a];
        }
        for (int b = 0; b < newParent.length; b++) {
            newParent[b] = parentList[b + i];
        }
        List<String []> res = new ArrayList<>();
        res.add(existedParent);
        res.add(newParent);

        return res;
    }

    private String getParentKey(String [] existedlist) {
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < existedlist.length; i ++) {
            if (i == existedlist.length - 1) {
                bf.append(existedlist[i]);
                break;
            }
            bf.append(existedlist[i] + File.separator);
        }
        String res = namekey_treemap.get(bf.toString());
        return res;
    }

    private Tree createRecursiveNewTree(String[] nameList, int level, String name, GitObject go) throws Exception {
        Tree root = new Tree(name);

        if (level == nameList.length - 1) {
            String fmt = go.getFmt();
            String n = go.getName();
            String key = go.getKey();
            if (fmt.equals("blob")) {
                root.getBlobMap().put(key, n);
                go.compressWrite();
            }
            else {
                root.getTreeMap().put(key, (Tree)go);
            }
            root.update();
            return root;
        }

        String nextTreeName = name + File.separator + nameList[level];
        root.getTreeMap().put(nextTreeName, createRecursiveNewTree(nameList, level + 1, nextTreeName, go));
        root.update();
        return root;
    }

    // add后依据传入的GitObject更新对应的 name_key_map 以及 valuemap
    private void updateMap(GitObject go, String date) throws Exception {
        String fmt = go.getFmt();
        String mode = go.getMode();
        String key = go.getKey();
        //文件名
        String name = go.getName();

        if (fmt.equals("blob")) {
            String value = mode + " " + key + " " + name + " " + date;
            valueMapAdd(name, value);
            namekey_blobmap.put(name, key);
        }

        else if (fmt.equals("tree")) {
            Tree t = (Tree)go;
            namekey_treemap.put(name, key);
            for (String blobKey : t.getBlobMap().keySet()) {
                Blob b = Blob.deserialize(blobKey);
                updateMap(b, date);
            }
            for (Tree tree : t.getTreeMap().values()) {
                updateMap(tree, date);
            }
        }
    }


    // Delete file from staging area (blobMap, key_name_map, valuemap).
    public void deleteFile(String filename) {
        String key = namekey_blobmap.get(filename);
        MapDelete(filename);
        // blobMap递归删除，并更新对应tree的值
        boolean flag = deleteBlob(key);
        if (!flag) System.out.println(filename + " not found.");

    }

    // Delete directory from staging area (root and valuemap).
    public void deleteDirectory(String dirname) {
        String key = namekey_treemap.get(dirname);
        MapDeleteDirectory(dirname, this);
        // treeMap递归删除，并更新对应tree的值
        boolean flag = deleteTree(key);
        if (!flag) System.out.println(dirname + " not found.");
    }

    @Override
    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    public void compressWriteAsTree() throws Exception {
        super.compressWrite();
    }

    // 展示暂存区(valuemap)现存的文件
    public void show() {
        for (String v : valuemap.values()) {
            System.out.println(v);
        }
    }

    // 展示name_key_map现存的文件
    public void showIndexMap() {
        namekey_blobmap.forEach((k, v) -> System.out.println("name: " + k + " key: " + v));
        namekey_treemap.forEach((k, v) -> System.out.println("name: " + k + " key: " + v));
    }

    // 向valuemap新增键值对.
    private void valueMapAdd(String name, String value) {
        // 如果文件名相同，而内容不同，会替换value，删除旧节点，尾部插入新节点.
        if (valuemap.containsKey(name)) {
            valuemap.remove(name);
        }
        valuemap.put(name,value);
    }

    // 找到对应的directory，然后执行MapDeleteTree操作
    private void MapDeleteDirectory(String dirname, Tree t) {
        if (!namekey_treemap.containsKey(dirname)) {
            System.out.println("No directory named" + dirname + "found in staging area(name_key_map).");
            return;
        }

        for (Tree tree : t.getTreeMap().values()) {
            String name = tree.getName();
            // 找到对应目标
            if (name.equals(dirname)) {
                MapDeleteTree(tree);
                namekey_treemap.remove(name);
            }
            // 未找到
            else {
                MapDeleteDirectory(dirname, tree);
            }
        }
    }

    // 递归删除Tree下包含的map(name_key_map, value_map)
    private void MapDeleteTree(Tree t) {
        for (String blobName : t.getBlobMap().values()) {
            MapDelete(blobName);
        }
        for (Tree tree : t.getTreeMap().values()) {
            MapDeleteTree(tree);
            namekey_treemap.remove(tree.name);
        }
    }

    // 删除Tree下map(name_key_map, value_map)中的文件索引.
    private void MapDelete(String name) {
        if (valuemap.containsKey(name)) {
            valuemap.remove(name);
        } else {
            System.out.println("No file named " + name + " found in staging area (valuemap)");
        }
        if (namekey_blobmap.containsKey(name)) {
            namekey_blobmap.remove(name);
        } else {
            System.out.println("No file named " + name + " found in staging area (name_key_map)");
        }
    }

    public void clear() {
        valuemap = new LinkedHashMap<>();
    }

}
