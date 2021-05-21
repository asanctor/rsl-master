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
import java.util.List;

public class GetTests {

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp() {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    @Before
    public void emptyDB()
    {
        db.emptyDatabase();
    }

    @AfterClass
    public static void tearDown() {
        db.close();
        try {
            Files.delete(Paths.get(dbPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getByID() {
        Point p1 = new Point(2, 3);
        db.save(p1);

        Point p2 = db.getByDatabaseID(Point.class, 1);
        Assert.assertEquals(2, p2.getX());
        Assert.assertEquals(3, p2.getY());

    }

    @Test
    public void getByInvalidID() {
        long id = Long.MAX_VALUE;
        Point p = db.getByID(Point.class, id);
        Assert.assertNull(p);
    }




}

