import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Main {

    static final String DIR_NAME = "serial";

    public static void main(String[] args) {
        Path directory = Path.of(DIR_NAME);
        if (!Files.isDirectory(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String beforeSer = "Строка для сериализации";
        String filename = serializeToFile(beforeSer);
        String afterSer = (String) (deserializeFromFile(filename));
        System.out.println(beforeSer.equals(afterSer));
    }

    public static <T extends Serializable> String serializeToFile(T object) {
        String filename = object.getClass().getName() + "_" + UUID.randomUUID().toString();
        Path file = Path.of(DIR_NAME + "/" + filename);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                System.out.printf("Не удалось создать файл %s.%n", filename);
                return null;
            }
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file));
            oos.writeObject(object);
            oos.close();
            return filename;
        } catch (IOException e) {
            System.out.printf("Не удалось записать объект %s в файл%s.%n", object.toString(), filename);
        }
        return null;
    }

    public static boolean isFilenameValid(String filename) {
        String regex = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";
        return filename.contains("_") && filename.substring(filename.lastIndexOf('_') + 1).matches(regex);
    }

    public static Object deserializeFromFile(String filename) {
        if (!isFilenameValid(filename)) {
            System.out.println("Имя файла должно соответствовать виду class.getName() + \"_\" + UUID.randomUUID().toString()");
            return null;
        }
        try {
            // Предполагается что мы хотим десериализовывать файлы только из отведённой под это папки
            Path file = Path.of(DIR_NAME + "/" + filename);
            if (Files.exists(file)) {
                ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file));
                Object object = ois.readObject();
                ois.close();
                Files.delete(file);
                return object;
            } else {
                System.out.printf("Файл %s не найден.", filename);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.printf("Не удалось прочитать объект из файла %s.%n", filename);
        }
        return null;
    }
}
