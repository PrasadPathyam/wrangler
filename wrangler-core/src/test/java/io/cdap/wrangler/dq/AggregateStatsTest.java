package io.cdap.wrangler.dq;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.TestingRig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test for AggregateStats directive.
 */
public class AggregateStatsTest {

  @Test
  public void testAggregateStatsTotal() throws Exception {
    // Step 1: Create input rows
    List<Row> input = Arrays.asList(
      new Row("size", "1MB").add("time", "1s"),
      new Row("size", "512KB").add("time", "500ms"),
      new Row("size", "1.5MB").add("time", "2.5s")
    );

    // Step 2: Define the recipe
    String[] recipe = new String[] {
      "aggregate-stats :size :time :total_size_mb :total_time_sec"
    };

    // Step 3: Execute the recipe
    List<Row> output = TestingRig.execute(recipe, input);

    // Step 4: Verify the output
    Assert.assertEquals(1, output.size());

    Row result = output.get(0);
    double totalSizeMB = (1 * 1024 * 1024 + 512 * 1024 + (long)(1.5 * 1024 * 1024)) / (1024.0 * 1024);
    double totalTimeSec = 1.0 + 0.5 + 2.5;

    Assert.assertEquals(totalSizeMB, (double) result.getValue("total_size_mb"), 0.001);
    Assert.assertEquals(totalTimeSec, (double) result.getValue("total_time_sec"), 0.001);
  }
}
