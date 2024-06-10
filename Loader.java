import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Loader {

    private static final Map<String, String> ugaBuga = new ConcurrentHashMap<>();
    private static String mainClass;
    public static void main(String[] args) {
        ugaBuga.forEach((name, bytes) -> {
            byte[] byteCode = loadClassBytes(new String(Base64.getDecoder().decode(name)), Base64.getDecoder().decode(bytes));
            System.out.println(Arrays.toString(byteCode));
            Class<?> loadedClass = defineClass(new String(Base64.getDecoder().decode(name)), byteCode);

            try {
                if(new String(Base64.getDecoder().decode(name)).equals(mainClass)){
                    Method mainMethod = loadedClass.getMethod(mainClass, String[].class);
                    String[] methodArgs = new String[0]; // Передайте здесь аргументы, если необходимо
                    mainMethod.invoke(null, (Object) methodArgs);

                    Method mainMethod1 = loadedClass.getMethod("main", String[].class);
                    mainMethod1.invoke(null, (Object) args);
                }
                ClassLoader.getSystemClassLoader().loadClass(new String(Base64.getDecoder().decode(name)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static byte[] loadClassBytes(String path, byte[] keys) {
        byte[] bytes;
        try {
            byte[] toDecrypt = Loader.class.getResourceAsStream("/"+path+".class").readAllBytes();
            System.out.println(keys[0]);
            System.out.println(keys[1]);
            System.out.println(keys[2]);
            System.out.println(keys[3]);
            System.out.println(keys[4]);
            System.out.println(keys[5]);
            System.out.println(keys[6]);
            for (int i = 0; i< toDecrypt.length; ++i){
                if(i>=4){
                    switch (i-4) {
                        case 0 -> toDecrypt[i] ^= keys[0];
                        case 1 -> toDecrypt[i] ^= keys[1];
                        case 2 -> toDecrypt[i] ^= keys[2];
                        case 3 -> toDecrypt[i] ^= keys[3];
                        case 4 -> toDecrypt[i] ^= keys[4];
                        case 5 -> toDecrypt[i] ^= keys[5];
                        default -> toDecrypt[i] ^= keys[6];
                    }
                }
            }
            bytes = toDecrypt;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    private static Class<?> defineClass(String name, byte[] byteCode) {
        try {
            return new ClassLoader() {
                public Class<?> loadClass() {
                    return defineClass(name.replace("/","."), byteCode, 0, byteCode.length);
                }
            }.loadClass(name.replace("/","."));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
