/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Represents a time duration token (e.g., "200ms", "2min").
 * Converts input to milliseconds for consistent downstream usage.
 */
public class TimeDuration implements Token {
  private final String rawValue;
  private final long milliseconds;

  public TimeDuration(String value) {
    this.rawValue = value;
    this.milliseconds = parseToMilliseconds(value);
  }

  private long parseToMilliseconds(String value) {
    value = value.trim().toLowerCase();

    if (value.endsWith("ms")) {
      return (long) Double.parseDouble(value.replace("ms", ""));
    } else if (value.endsWith("s")) {
      return (long) (Double.parseDouble(value.replace("s", "")) * 1000);
    } else if (value.endsWith("min")) {
      return (long) (Double.parseDouble(value.replace("min", "")) * 60 * 1000);
    } else if (value.endsWith("h")) {
      return (long) (Double.parseDouble(value.replace("h", "")) * 60 * 60 * 1000);
    } else {
      throw new IllegalArgumentException("Invalid time duration format: " + value);
    }
  }

  public long getMilliseconds() {
    return milliseconds;
  }

  @Override
  public Object value() {
    return milliseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(milliseconds);
  }

  @Override
  public String toString() {
    return rawValue + " (" + milliseconds + " ms)";
  }
}
