package io.github.gdx945;

import java.io.IOException;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-02 21:29:53
 * @since : 0.1
 */
public class Test {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        //Write Obj to file
//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tempFile"));
//        oos.writeObject(RpcMethod.getInst("KKK"));
//
//        //Read Obj from file
//        File file = new File("tempFile");
//        ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(file));
//
//        //判断是否是同一个对象
//        System.out.println(ois.readObject() == RpcMethod.getInst("KKK"));

        String a = "xx:xxxx";
        String[] ss = new String[]{"xz", null, null};
        System.arraycopy(a.split(":"), 0, ss, 1, 2);
        System.out.println(ss[0]);
        System.out.println(ss[1]);
        System.out.println(ss[2]);
    }
}
