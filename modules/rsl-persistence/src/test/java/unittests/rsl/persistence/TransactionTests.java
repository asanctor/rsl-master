package unittests.rsl.persistence;

import org.junit.*;
import rsl.persistence.PersistenceFactory;
import rsl.persistence.RslPersistence;
import rsl.util.Random;
import rsl.util.TempDir;
import unittests.rsl.persistence.DataClasses.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TransactionTests {

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    @Before
    public void clearDatabase()
    {
        db.emptyDatabase();
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
    public void TransactionTest()
    {

        boolean started;
        boolean stopped;

        started = db.startTransaction();
        Assert.assertEquals(started, true);

        Point p1 = new Point(0,0);
        db.save(p1);

        started = db.startTransaction();
        Assert.assertEquals(started, false);

        Point p2 = new Point(0,0);
        db.save(p2);

        stopped = db.endTransaction();
        Assert.assertEquals(stopped, false);

        stopped = db.endTransaction();
        Assert.assertEquals(stopped, true);

    }



}
