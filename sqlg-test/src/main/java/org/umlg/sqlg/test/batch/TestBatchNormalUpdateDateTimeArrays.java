package org.umlg.sqlg.test.batch;

import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Assert;
import org.junit.Test;
import org.umlg.sqlg.test.BaseTest;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2017/01/26
 * Time: 12:22 PM
 */
public class TestBatchNormalUpdateDateTimeArrays extends BaseTest {

    @Test
    public void testUpdateLocalDateTimeArray() {
        this.sqlgGraph.tx().normalBatchModeOn();
        LocalDateTime[] localDateTimeArray = new LocalDateTime[]{LocalDateTime.now(), LocalDateTime.now()};
        Vertex a1 = this.sqlgGraph.addVertex(T.label, "A", "localDateTimeArray1", localDateTimeArray);
        Vertex a2 = this.sqlgGraph.addVertex(T.label, "A", "localDateTimeArray2", localDateTimeArray);
        Vertex a3 = this.sqlgGraph.addVertex(T.label, "A", "localDateTimeArray3", localDateTimeArray);
        this.sqlgGraph.tx().commit();

        this.sqlgGraph.tx().normalBatchModeOn();
        LocalDateTime[] localDateTimeArrayAgain = new LocalDateTime[]{LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)};
        a1.property("localDateTimeArray1", localDateTimeArrayAgain);
        a2.property("localDateTimeArray2", localDateTimeArrayAgain);
        a3.property("localDateTimeArray3", localDateTimeArrayAgain);
        this.sqlgGraph.tx().commit();

        a1 = this.sqlgGraph.traversal().V(a1.id()).next();
        a2 = this.sqlgGraph.traversal().V(a2.id()).next();
        a3 = this.sqlgGraph.traversal().V(a3.id()).next();
        Assert.assertArrayEquals(localDateTimeArrayAgain, a1.value("localDateTimeArray1"));
        Assert.assertFalse(a1.property("localDateTimeArray2").isPresent());
        Assert.assertFalse(a1.property("localDateTimeArray3").isPresent());

        Assert.assertFalse(a2.property("localDateTimeArray1").isPresent());
        Assert.assertArrayEquals(localDateTimeArrayAgain, a2.value("localDateTimeArray2"));
        Assert.assertFalse(a2.property("localDateTimeArray3").isPresent());

        Assert.assertFalse(a3.property("localDateTimeArray1").isPresent());
        Assert.assertFalse(a3.property("localDateTimeArray2").isPresent());
        Assert.assertArrayEquals(localDateTimeArrayAgain, a3.value("localDateTimeArray3"));

    }

    @Test
    public void testUpdateLocalDateArray() {
        this.sqlgGraph.tx().normalBatchModeOn();
        LocalDate[] localDateArray = new LocalDate[]{LocalDate.now(), LocalDate.now()};
        Vertex a1 = this.sqlgGraph.addVertex(T.label, "A", "localDateArray1", localDateArray);
        Vertex a2 = this.sqlgGraph.addVertex(T.label, "A", "localDateArray2", localDateArray);
        Vertex a3 = this.sqlgGraph.addVertex(T.label, "A", "localDateArray3", localDateArray);
        this.sqlgGraph.tx().commit();

        this.sqlgGraph.tx().normalBatchModeOn();
        LocalDate[] localDateArrayAgain = new LocalDate[]{LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)};
        a1.property("localDateArray1", localDateArrayAgain);
        a2.property("localDateArray2", localDateArrayAgain);
        a3.property("localDateArray3", localDateArrayAgain);
        this.sqlgGraph.tx().commit();

        a1 = this.sqlgGraph.traversal().V(a1.id()).next();
        a2 = this.sqlgGraph.traversal().V(a2.id()).next();
        a3 = this.sqlgGraph.traversal().V(a3.id()).next();
        Assert.assertArrayEquals(localDateArrayAgain, a1.value("localDateArray1"));
        Assert.assertFalse(a1.property("localDateArray2").isPresent());
        Assert.assertFalse(a1.property("localDateArray3").isPresent());

        Assert.assertFalse(a2.property("localDateArray1").isPresent());
        Assert.assertArrayEquals(localDateArrayAgain, a2.value("localDateArray2"));
        Assert.assertFalse(a2.property("localDateArray3").isPresent());

        Assert.assertFalse(a3.property("localDateArray1").isPresent());
        Assert.assertFalse(a3.property("localDateArray2").isPresent());
        Assert.assertArrayEquals(localDateArrayAgain, a3.value("localDateArray3"));

    }

    @Test
    public void testUpdateLocalTimeArray() {
        this.sqlgGraph.tx().normalBatchModeOn();
        LocalTime[] localTimeArray = new LocalTime[]{LocalTime.now(), LocalTime.now()};
        Vertex a1 = this.sqlgGraph.addVertex(T.label, "A", "localTimeArray1", localTimeArray);
        Vertex a2 = this.sqlgGraph.addVertex(T.label, "A", "localTimeArray2", localTimeArray);
        Vertex a3 = this.sqlgGraph.addVertex(T.label, "A", "localTimeArray3", localTimeArray);
        this.sqlgGraph.tx().commit();

        this.sqlgGraph.tx().normalBatchModeOn();
        LocalTime[] localTimeArrayAgain = new LocalTime[]{LocalTime.now().plusHours(1), LocalTime.now().plusHours(2)};
        a1.property("localTimeArray1", localTimeArrayAgain);
        a2.property("localTimeArray2", localTimeArrayAgain);
        a3.property("localTimeArray3", localTimeArrayAgain);
        this.sqlgGraph.tx().commit();

        a1 = this.sqlgGraph.traversal().V(a1.id()).next();
        a2 = this.sqlgGraph.traversal().V(a2.id()).next();
        a3 = this.sqlgGraph.traversal().V(a3.id()).next();

        List<LocalTime> localTimes1 = new ArrayList<>();
        for (LocalTime localTime : localTimeArrayAgain) {
            localTimes1.add(localTime.minusNanos(localTime.getNano()));
        }
        Assert.assertArrayEquals(localTimes1.toArray(), a1.value("localTimeArray1"));
        Assert.assertFalse(a1.property("localTimeArray2").isPresent());
        Assert.assertFalse(a1.property("localTimeArray3").isPresent());

        Assert.assertFalse(a2.property("localTimeArray1").isPresent());
        Assert.assertArrayEquals(localTimes1.toArray(), a2.value("localTimeArray2"));
        Assert.assertFalse(a2.property("localTimeArray3").isPresent());

        Assert.assertFalse(a3.property("localTimeArray1").isPresent());
        Assert.assertFalse(a3.property("localTimeArray2").isPresent());
        Assert.assertArrayEquals(localTimes1.toArray(), a3.value("localTimeArray3"));

    }

    @Test
    public void testUpdateZonedDateTimeArray() {
        this.sqlgGraph.tx().normalBatchModeOn();
        ZonedDateTime[] zonedDateTimeArray = new ZonedDateTime[]{ZonedDateTime.now(), ZonedDateTime.now()};
        Vertex a1 = this.sqlgGraph.addVertex(T.label, "A", "zonedDateTimeArray1", zonedDateTimeArray);
        Vertex a2 = this.sqlgGraph.addVertex(T.label, "A", "zonedDateTimeArray2", zonedDateTimeArray);
        Vertex a3 = this.sqlgGraph.addVertex(T.label, "A", "zonedDateTimeArray3", zonedDateTimeArray);
        this.sqlgGraph.tx().commit();

        this.sqlgGraph.tx().normalBatchModeOn();
        ZonedDateTime[] zonedDateTimeArrayAgain = new ZonedDateTime[]{ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusHours(2)};
        a1.property("zonedDateTimeArray1", zonedDateTimeArrayAgain);
        a2.property("zonedDateTimeArray2", zonedDateTimeArrayAgain);
        a3.property("zonedDateTimeArray3", zonedDateTimeArrayAgain);
        this.sqlgGraph.tx().commit();

        a1 = this.sqlgGraph.traversal().V(a1.id()).next();
        a2 = this.sqlgGraph.traversal().V(a2.id()).next();
        a3 = this.sqlgGraph.traversal().V(a3.id()).next();

        Assert.assertArrayEquals(zonedDateTimeArrayAgain, a1.value("zonedDateTimeArray1"));
        Assert.assertFalse(a1.property("zonedDateTimeArray2").isPresent());
        Assert.assertFalse(a1.property("zonedDateTimeArray3").isPresent());

        Assert.assertFalse(a2.property("zonedDateTimeArray1").isPresent());
        Assert.assertArrayEquals(zonedDateTimeArrayAgain, a2.value("zonedDateTimeArray2"));
        Assert.assertFalse(a2.property("zonedDateTimeArray3").isPresent());

        Assert.assertFalse(a3.property("zonedDateTimeArray1").isPresent());
        Assert.assertFalse(a3.property("zonedDateTimeArray2").isPresent());
        Assert.assertArrayEquals(zonedDateTimeArrayAgain, a3.value("zonedDateTimeArray3"));
    }

    @Test
    public void testUpdateDurationArray() {
        this.sqlgGraph.tx().normalBatchModeOn();
        Duration[] durationArray = new Duration[]{Duration.ofDays(1), Duration.ofDays(1)};
        Vertex a1 = this.sqlgGraph.addVertex(T.label, "A", "durationArray1", durationArray);
        Vertex a2 = this.sqlgGraph.addVertex(T.label, "A", "durationArray2", durationArray);
        Vertex a3 = this.sqlgGraph.addVertex(T.label, "A", "durationArray3", durationArray);
        this.sqlgGraph.tx().commit();

        this.sqlgGraph.tx().normalBatchModeOn();
        Duration[] durationArrayAgain = new Duration[]{Duration.ofDays(3), Duration.ofDays(4)};
        a1.property("durationArray1", durationArrayAgain);
        a2.property("durationArray2", durationArrayAgain);
        a3.property("durationArray3", durationArrayAgain);
        this.sqlgGraph.tx().commit();

        a1 = this.sqlgGraph.traversal().V(a1.id()).next();
        a2 = this.sqlgGraph.traversal().V(a2.id()).next();
        a3 = this.sqlgGraph.traversal().V(a3.id()).next();

        Assert.assertArrayEquals(durationArrayAgain, a1.value("durationArray1"));
        Assert.assertFalse(a1.property("durationArray2").isPresent());
        Assert.assertFalse(a1.property("durationArray3").isPresent());

        Assert.assertFalse(a2.property("durationArray1").isPresent());
        Assert.assertArrayEquals(durationArrayAgain, a2.value("durationArray2"));
        Assert.assertFalse(a2.property("durationArray3").isPresent());

        Assert.assertFalse(a3.property("durationArray1").isPresent());
        Assert.assertFalse(a3.property("durationArray2").isPresent());
        Assert.assertArrayEquals(durationArrayAgain, a3.value("durationArray3"));
    }

    @Test
    public void testUpdatePeriodArray() {
        this.sqlgGraph.tx().normalBatchModeOn();
        Period[] periodArray = new Period[]{Period.of(1, 1, 1), Period.of(1, 1, 1)};
        Vertex a1 = this.sqlgGraph.addVertex(T.label, "A", "periodArray1", periodArray);
        Vertex a2 = this.sqlgGraph.addVertex(T.label, "A", "periodArray2", periodArray);
        Vertex a3 = this.sqlgGraph.addVertex(T.label, "A", "periodArray3", periodArray);
        this.sqlgGraph.tx().commit();

        this.sqlgGraph.tx().normalBatchModeOn();
        Period[] periodArrayAgain = new Period[]{Period.of(2, 2, 2), Period.of(4, 4, 4)};
        a1.property("periodArray1", periodArrayAgain);
        a2.property("periodArray2", periodArrayAgain);
        a3.property("periodArray3", periodArrayAgain);
        this.sqlgGraph.tx().commit();

        a1 = this.sqlgGraph.traversal().V(a1.id()).next();
        a2 = this.sqlgGraph.traversal().V(a2.id()).next();
        a3 = this.sqlgGraph.traversal().V(a3.id()).next();

        Assert.assertArrayEquals(periodArrayAgain, a1.value("periodArray1"));
        Assert.assertFalse(a1.property("periodArray2").isPresent());
        Assert.assertFalse(a1.property("periodArray3").isPresent());

        Assert.assertFalse(a2.property("periodArray1").isPresent());
        Assert.assertArrayEquals(periodArrayAgain, a2.value("periodArray2"));
        Assert.assertFalse(a2.property("periodArray3").isPresent());

        Assert.assertFalse(a3.property("periodArray1").isPresent());
        Assert.assertFalse(a3.property("periodArray2").isPresent());
        Assert.assertArrayEquals(periodArrayAgain, a3.value("periodArray3"));
    }
}