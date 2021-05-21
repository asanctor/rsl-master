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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddTests {

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    //@Before
    //public void closeExistingTransaction()
    //{
    //   db.forceEndTransaction();
    //}

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
    public void addOnePointAutomaticTransaction()
    {

        int x = 25;
        int y = 50;

        Point p1 = new Point(x,y);
        db.save(p1);

        List<Point> result = db.getEntitiesByClass(Point.class);

        Assert.assertEquals(1, result.size());

        Point p2 = result.get(0);

        Assert.assertEquals(x, p2.getX());
        Assert.assertEquals(y, p2.getY());

    }

    @Test
    public void addOnePointManualTransaction()
    {

        int x = 25;
        int y = 50;

        Point p1 = new Point(x,y);

        db.startTransaction();
        db.save(p1);
        db.endTransaction();

        List<Point> result = db.getEntitiesByClass(Point.class);

        Assert.assertEquals(1, result.size());

        Point p2 = result.get(0);

        Assert.assertEquals(x, p2.getX());
        Assert.assertEquals(y, p2.getY());

    }

    @Test
    public void addManyPointsAutomaticTransaction()
    {

        for(int i = 0; i < 1000; i++)
        {
            db.save(new Point(Random.randomInt(0, 500), Random.randomInt(0, 500)));
        }

        List<Point> result = db.getEntitiesByClass(Point.class);

        Assert.assertEquals(1000, result.size());

    }

    @Test
    public void addManyPointsManualTransaction()
    {

        db.startTransaction();
        for(int i = 0; i < 1000; i++)
        {
            db.save(new Point(Random.randomInt(0, 500), Random.randomInt(0, 500)));
        }
        db.endTransaction();

        List<Point> result = db.getEntitiesByClass(Point.class);

        Assert.assertEquals(1000, result.size());

    }







}
