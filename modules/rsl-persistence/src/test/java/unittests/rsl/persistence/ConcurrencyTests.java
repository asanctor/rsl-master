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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class ConcurrencyTests {

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

    // the following test will start 10 treads that simultaneously add a random amount of data to the database
    @Test
    public void asyncDataAdd()
    {
        final int threadCount = 10;
        final int taskCount = threadCount;

        ExecutorService exec =  Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger totalAdded = new AtomicInteger(0);

        Runnable dbTask = () -> {
            int loopCount = Random.randomInt(3, 10);
            for(int i = 0; i < loopCount; i++)
            {
                int dataAmount = Random.randomInt(1, 100);
                db.startTransaction();
                for(int j = 0; j < dataAmount; j++)
                {
                    db.save(new Point(Random.randomInt(0, 100), Random.randomInt(0, 100)));
                }
                db.endTransaction();
                totalAdded.updateAndGet( n -> n + dataAmount);
            }
            latch.countDown();
        };

        try {
            for(int i = 0; i < taskCount; i++)
            {
                exec.execute(dbTask);
            }
            latch.await();
            exec.shutdown();
        } catch(Exception e) {
            Assert.fail();
        }

        long count = db.countByClass(Point.class);
        Assert.assertEquals(totalAdded.get(), count);
    }







}

