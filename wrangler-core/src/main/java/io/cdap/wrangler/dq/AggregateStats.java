package io.cdap.wrangler.dq;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.parser.TimeDuration;

import java.util.ArrayList;
import java.util.List;

/**
 * A directive to compute total byte size and time duration from columns containing unit strings.
 */
public class AggregateStats implements Directive, Executor<List<Row>, List<Row>> {
  private String sizeColumn;
  private String timeColumn;
  private String outputSizeColumn;
  private String outputTimeColumn;

  private long totalBytes = 0;
  private long totalMillis = 0;
  private int rowCount = 0;

  @Override
public UsageDefinition define() {
  UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
  builder.define("size_column", TokenType.COLUMN_NAME);
  builder.define("time_column", TokenType.COLUMN_NAME);
  builder.define("output_size_column", TokenType.COLUMN_NAME);
  builder.define("output_time_column", TokenType.COLUMN_NAME);
  return builder.build();
}


  @Override
  public void initialize(Arguments arguments) {
    this.sizeColumn = arguments.value("size_column").toString().substring(1);   // remove leading ":"
    this.timeColumn = arguments.value("time_column").toString().substring(1);
    this.outputSizeColumn = arguments.value("output_size_column").toString().substring(1);
    this.outputTimeColumn = arguments.value("output_time_column").toString().substring(1);
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context){
    for (Row row : rows) {
      Object sizeVal = row.getValue(sizeColumn);
      Object timeVal = row.getValue(timeColumn);

      if (sizeVal instanceof String) {
        ByteSize byteSize = new ByteSize((String) sizeVal);
        totalBytes += byteSize.getBytes();
      }

      if (timeVal instanceof String) {
        TimeDuration timeDuration = new TimeDuration((String) timeVal);
        totalMillis += timeDuration.getMilliseconds();
      }

      rowCount++;
    }

    List<Row> result = new ArrayList<>();
    Row out = new Row();
    out.add(outputSizeColumn, totalBytes / (1024.0 * 1024)); // bytes → MB
    out.add(outputTimeColumn, totalMillis / 1000.0);         // ms → seconds
    result.add(out);
    return result;
  }

  @Override
  public void destroy() {
    // No-op
  }
}
