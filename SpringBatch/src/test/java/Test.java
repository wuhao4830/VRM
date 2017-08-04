/**
 * Created by wuhao on 15/12/23.
 */
public class Test {
    public static void main(String args[]){
        String maches="^\\d{18}$";
        String str="00000000023434523";
        System.out.println(str.matches(maches));
        System.out.println("呵呵哒");
    }
}
