package unittests.rsl.persistence;

import org.junit.*;
import rsl.persistence.RslPersistence;
import rsl.persistence.PersistenceFactory;
import rsl.util.Random;
import rsl.util.TempDir;
import unittests.rsl.persistence.DataClasses.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateTests {

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    @AfterClass
    public static void tearDown()
    {
        db.close();
        try {
            Files.delete(Paths.get(dbPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateOnePoint() {
        Point p1 = new Point(2, 3);
        db.save(p1);

        Point p2 = db.getByDatabaseID(Point.class, 1);
        db.startTransaction();
        p2.setX(5);
        p2.setY(6);
        db.endTransaction();

        Point p3 = db.getByDatabaseID(Point.class, 1);
        Assert.assertEquals(5, p3.getX());
        Assert.assertEquals(6, p3.getY());
    }

    @Test
    public void updateOnePointNestedTransactions() {
        Point p1 = new Point(4, 4);
        db.save(p1);

        db.startTransaction();
            Point p2 = db.getByDatabaseID(Point.class, 2);
            p2.setX(5);
            p2.setY(6);
        db.endTransaction();

        Point p3 = db.getByDatabaseID(Point.class, 2);
        Assert.assertEquals(5, p3.getX());
        Assert.assertEquals(6, p3.getY());
    }

}
