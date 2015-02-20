package org.umlg.sqlg.test;

import org.apache.tinkerpop.gremlin.process.T;
import org.junit.Test;

/**
 * Date: 2014/07/16
 * Time: 7:43 PM
 */
public class TestPool extends BaseTest {

    //TODO this test nothing, needs to multi threaded
    @Test
    public void testSqlGraphConnectionsDoesNotExhaustPool() {
        for (int i = 0; i < 1000; i++) {
            this.sqlgGraph.addVertex(T.label, "Person");
        }
        this.sqlgGraph.tx().commit();
        for (int i = 0; i < 1000; i++) {
            this.sqlgGraph.V().has(T.label, "Person").hasNext();
        }
    }
}
